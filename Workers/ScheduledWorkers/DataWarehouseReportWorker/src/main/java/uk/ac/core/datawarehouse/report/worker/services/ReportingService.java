package uk.ac.core.datawarehouse.report.worker.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Search;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import uk.ac.core.datawarehouse.report.worker.model.*;

/**
 * @author lucas
 */
@Service
public class ReportingService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    JestClient jestClient;

    public ReportingService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        JestClientFactory factory = new JestClientFactory();
        factory.setHttpClientConfig(new HttpClientConfig.Builder("http://core-indx-pcr01.open.ac.uk:9200")
                .readTimeout(60000)
                .multiThreaded(true)
                .build());
        this.jestClient = factory.getObject();
    }

    private static final Logger LOG = Logger.getLogger("RepositoriesReportingService");

    public ReportDataProviders getRepositoriesList() {

        String SQL = "SELECT re.id_repository AS core_id,"
                + "re.id_opendoar AS id_opendoar,"
                + "re.id_roar AS id_roar,"
                + "re.name AS name, "
                + "rcj.id_jisc AS id_jisc,"
                + "rl.country_code AS country_code, "
                + "re.metadata_format AS metadata_format, "
                + "re.created_date AS created_date, "
                + "DATE_FORMAT(created_date, '%d/%m/%Y') AS repository_core_inclusion_date, "
                + "rec.id_repository AS recommender_id_repository, "
                + "dash.id AS dashboard_id "
                + "FROM repository re LEFT JOIN repository_core_jisc rcj ON (re.id_repository=rcj.id_repository) "
                + "INNER JOIN repository_location rl ON (re.id_repository=rl.id_repository) "
                + "LEFT JOIN recommender rec ON (re.id_repository = rec.id_repository) "
                + "LEFT JOIN DashboardRepo dash ON (re.id_repository = dash.id)";

        List<ReportRepository> allReposInDb = jdbcTemplate.query(SQL, new RowMapper<ReportRepository>() {
            @Override
            public ReportRepository mapRow(ResultSet rs, int rowNum) throws SQLException {
                ReportRepository reportRepository = new ReportRepository();
                reportRepository.setCORE_ID(rs.getInt("core_id"));
                reportRepository.setOpenDOAR_ID(rs.getInt("id_opendoar"));
//                reportRepository.setROAR_ID(rs.getInt("id_roar"));
                reportRepository.setName(rs.getString("name"));
                reportRepository.setInstitution("N/A");
                reportRepository.setCountry(rs.getString("country_code"));
                reportRepository.setJisc_ID(rs.getInt("id_jisc"));
                String metadataFormat = rs.getString("metadata_format");
                reportRepository.setRioxx_enabled(metadataFormat.equalsIgnoreCase("rioxx"));
                String created_date = rs.getString("repository_core_inclusion_date");
                reportRepository.setRepository_core_inclusion_date(created_date);

                Integer recommender_id_repository = rs.getInt("recommender_id_repository");
                reportRepository.setRecommender_registered(recommender_id_repository != 0);// 0 == NULL in db

                Integer dashboard_id = rs.getInt("dashboard_id");
                reportRepository.setRepository_dahboard_registered(dashboard_id != 0);

                return reportRepository;
            }

        });

        try {

            Map<Integer, Integer> metadataCounts = this.getCountsOfMetadataOfRepositories();
            Map<Integer, Integer> fulltextCounts = this.getCountsOfFullTextOfRepositories();

            for (ReportRepository repository : allReposInDb) {

                System.out.println("r:" + repository.toString());

                //set the counts from ES here
                Integer mCount = metadataCounts.get(repository.getCORE_ID());
                Integer ftCount = fulltextCounts.get(repository.getCORE_ID());
                repository.setCount_metadata(mCount != null ? mCount : 0);
                repository.setCount_fulltext(ftCount != null ? ftCount : 0);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.log(Level.ALL, e.getMessage());
        }

        ReportDataProviders reportDataProviders=new ReportDataProviders();
        reportDataProviders.setDataProvidersList(allReposInDb);
        return reportDataProviders;
    }

    private Map<Integer, Integer> getCountsOfMetadataOfRepositories() throws IOException {

        String elasticSearchQueryString = "{\n"
                + "  \"size\": 0,\n"
                + "  \"query\": {\n"
                + "    \"bool\": {\n"
                + "      \"must\": [\n"
                + "        {\n"
                + "          \"term\": {\n"
                + "            \"deleted\": {\n"
                + "              \"value\": \"ALLOWED\"\n"
                + "            }\n"
                + "          }\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"aggs\": {\n"
                + "    \"repositories_aggs\": {\n"
                + "      \"nested\": {\n"
                + "        \"path\": \"repositories\"\n"
                + "      },\n"
                + "      \"aggs\": {\n"
                + "        \"repositories_aggs_inner\": {\n"
                + "          \"terms\": {\n"
                + "            \"field\": \"repositories.id\",\n"
                + "            \"size\": 10000\n"
                + "          }\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}";

        Search search = new Search.Builder(elasticSearchQueryString)
                .addIndex("articles")
                .addType("article")
                .build();

        io.searchbox.core.SearchResult searchResult = jestClient.execute(search);
        if (searchResult != null) {
            Map<Integer, Integer> results = new HashMap<Integer, Integer>();

            JsonArray buckets = searchResult.getJsonObject()
                    .getAsJsonObject("aggregations")
                    .getAsJsonObject("repositories_aggs")
                    .getAsJsonObject("repositories_aggs_inner")
                    .getAsJsonArray("buckets");
            for (JsonElement bucket : buckets) {
                JsonObject bucketObj = bucket.getAsJsonObject();
                Integer key = bucketObj.get("key").getAsInt();
                Integer doc_count = bucketObj.get("doc_count").getAsInt();
                results.put(key, doc_count);

            }
            return results;
        }

        return null;
    }

    private Map<Integer, Integer> getCountsOfFullTextOfRepositories() throws IOException {

        String elasticSearchQueryString = "{\n"
                + "  \"size\": 0,\n"
                + "  \"query\": {\n"
                + "    \"bool\": {\n"
                + "      \"must\": [\n"
                + "        {\n"
                + "          \"term\": {\n"
                + "            \"deleted\": {\n"
                + "              \"value\": \"ALLOWED\"\n"
                + "            }\n"
                + "          }\n"
                + "        },\n"
                + "        {\n"
                + "          \"exists\": {\n"
                + "            \"field\": \"fullText\"\n"
                + "          }\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"aggs\": {\n"
                + "    \"repositories_aggs\": {\n"
                + "      \"nested\": {\n"
                + "        \"path\": \"repositories\"\n"
                + "      },\n"
                + "      \"aggs\": {\n"
                + "        \"repositories_aggs_inner\": {\n"
                + "          \"terms\": {\n"
                + "            \"field\": \"repositories.id\",\n"
                + "            \"size\": 10000\n"
                + "          }\n"
                + "        }\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}";

        Search search = new Search.Builder(elasticSearchQueryString)
                .addIndex("articles")
                .addType("article")
                .build();

        io.searchbox.core.SearchResult searchResult = jestClient.execute(search);
        if (searchResult != null) {
            Map<Integer, Integer> results = new HashMap<Integer, Integer>();

            JsonArray buckets = searchResult.getJsonObject()
                    .getAsJsonObject("aggregations")
                    .getAsJsonObject("repositories_aggs")
                    .getAsJsonObject("repositories_aggs_inner")
                    .getAsJsonArray("buckets");
            for (JsonElement bucket : buckets) {
                JsonObject bucketObj = bucket.getAsJsonObject();
                Integer key = bucketObj.get("key").getAsInt();
                Integer doc_count = bucketObj.get("doc_count").getAsInt();
                results.put(key, doc_count);

            }
            return results;
        }

        return null;
    }

    public ReportDataProviderAggregation getReportDataProviderAggregation() {

        ReportDataProviderAggregation reportDataProviderAggregation = new ReportDataProviderAggregation();

//        Integer count_of_data_providers;
        String COUNT_OF_DATA_PROVIDERS = "SELECT COUNT(*) FROM repository";
        Integer count_of_data_providers = jdbcTemplate.queryForObject(COUNT_OF_DATA_PROVIDERS, Integer.class);
        reportDataProviderAggregation.setCount_of_data_providers(count_of_data_providers);

//        Integer count_of_active_data_providers;
        String COUNT_OF_ACTIVE_DATA_PROVIDERS = "SELECT COUNT(*) FROM repository WHERE disabled=0";
        Integer count_of_active_data_providers = jdbcTemplate.queryForObject(COUNT_OF_ACTIVE_DATA_PROVIDERS, Integer.class);
        reportDataProviderAggregation.setCount_of_active_data_providers(count_of_active_data_providers);

//        Integer Count_of_publisher_connector_providers;
        String COUNT_OF_PUBLISHER_CONNECTOR_PROVIDERS = "SELECT COUNT(*) FROM repository WHERE source='dit'";
        Integer count_of_publisher_connector_providers = jdbcTemplate.queryForObject(COUNT_OF_PUBLISHER_CONNECTOR_PROVIDERS, Integer.class);
        reportDataProviderAggregation.setCount_of_publisher_connector_providers(count_of_publisher_connector_providers);

//        Integer Count_of_UK_repositories;
        String COUNT_OF_UK_REPOSITORIES = "SELECT count(*) FROM repository_location WHERE country_code = 'gb'";
        Integer count_of_UK_repositories = jdbcTemplate.queryForObject(COUNT_OF_UK_REPOSITORIES, Integer.class);
        reportDataProviderAggregation.setCount_of_UK_repositories(count_of_UK_repositories);

//        Integer Count_of_Rest_of_World_repositories;
        Integer Count_of_Rest_of_World_repositories = count_of_active_data_providers - count_of_UK_repositories;
        reportDataProviderAggregation.setCount_of_Rest_of_World_repositories(Count_of_Rest_of_World_repositories);

        String EUROPE_SQL = "SELECT count(*) FROM repository_location WHERE country_code IN (\n"
                + "    'al','AT','BA','BE','BG','BY','CH','CY',\n"
                + "'CZ','de','DK','EE','es','fi','fr','GB',\n"
                + "'ge','GR','HR','HU','ie','IS','IT','lt',\n"
                + "'LU','LV','md','MK','MT','NL','NO','PL',\n"
                + "'PT','RO','RS','RU','se','SI','ua'\n"
                + "    )";
        Integer europeCount = jdbcTemplate.queryForObject(EUROPE_SQL, Integer.class);
        reportDataProviderAggregation.setCount_of_active_Europe_repositories(europeCount);

        String NORTH_AMERICA_SQL = "SELECT COUNT(*) FROM repository_location WHERE country_code in (\n"
                + "    'ca','CR','CU','do','GP','GT','HN','JM',\n"
                + "'MX','NI','um','us'\n"
                + "    )";
        Integer namericaCount = jdbcTemplate.queryForObject(NORTH_AMERICA_SQL, Integer.class);
        reportDataProviderAggregation.setCount_of_active_North_America_repositories(namericaCount);

        String SOUTH_AMERICA_SQL = "SELECT COUNT(*) FROM repository_location WHERE country_code in (\n"
                + "    'ar','bo','br','CL','co','ec','gf','PE',\n"
                + "'sv','uy','ve'\n"
                + "    )";
        Integer samericaCount = jdbcTemplate.queryForObject(SOUTH_AMERICA_SQL, Integer.class);
        reportDataProviderAggregation.setCount_of_active_South_America_repositories(samericaCount);

        String ASIA_SQL = "SELECT count(*) from repository_location where country_code in ('AZ',\n"
                + "'BD','cn','hk','ID','il','in','IR','JP',\n"
                + "'KG','KR','KZ','lb','LK','my','np','pa',\n"
                + "'ph','PK','ps','QA','SA','SG','th','tr',\n"
                + "'TW')";
        Integer asiaCount = jdbcTemplate.queryForObject(ASIA_SQL, Integer.class);
        reportDataProviderAggregation.setCount_of_active_Asia_repositories(asiaCount);

        String OCEANIA_SQL = "SELECT COUNT(*) FROM repository_location WHERE country_code in (\n"
                + "    'AU','FJ','nc','NZ'\n"
                + "    )";
        Integer oceaniaCount = jdbcTemplate.queryForObject(OCEANIA_SQL, Integer.class);
        reportDataProviderAggregation.setCount_of_active_Oceania_repositories(oceaniaCount);

        String AFRICA_SQL = "SELECT count(*) from repository_location where country_code in ('BW',\n"
                + "'cm','CV','DZ','EG','ET','GH','KE','LS',\n"
                + "'ma','mz','NA','ng','RW','SD','SN','tz',\n"
                + "'UG','ZA''ZW')";
        Integer africaCount = jdbcTemplate.queryForObject(AFRICA_SQL, Integer.class);
        reportDataProviderAggregation.setCount_of_active_Africa_repositories(africaCount);

        String UNKNOWN_LOCATION_SQL = "SELECT COUNT(*)\n"
                + "FROM repository r LEFT JOIN repository_location rl ON (r.id_repository=rl.id_repository)\n"
                + "WHERE rl.country_code IS NULL";
        Integer unknownCount = jdbcTemplate.queryForObject(UNKNOWN_LOCATION_SQL, Integer.class);
        reportDataProviderAggregation.setCount_of_active_location_uknown_repositories(unknownCount);

        return reportDataProviderAggregation;

    }

    public ReportContent getContentReport() {
        ReportContent reportContent = new ReportContent();

        String TOTAL_QUERY = "{\n"
                + "  \"query\":{\n"
                + "    \"term\": {\n"
                + "      \"deleted\": {\n"
                + "        \"value\": \"ALLOWED\"\n"
                + "      }\n"
                + "    }\n"
                + "  },\n"
                + "  \"size\":0\n"
                + "}";

        String METADATA_ONLY_QUERY = "{\n"
                + "  \"query\": {\n"
                + "    \"bool\": {\n"
                + "      \"must\": [\n"
                + "        {\n"
                + "          \"term\": {\n"
                + "            \"deleted\": {\n"
                + "              \"value\": \"ALLOWED\"\n"
                + "            }\n"
                + "          }\n"
                + "        }\n"
                + "      ],\n"
                + "      \"must_not\": [\n"
                + "        {\n"
                + "          \"exists\": {\n"
                + "            \"field\": \"fullText\"\n"
                + "          }\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"size\":0\n"
                + "}";

        String FULL_RECORDS_QUERY = "{\n"
                + "  \"query\": {\n"
                + "    \"bool\": {\n"
                + "      \"must\": [\n"
                + "        {\n"
                + "          \"term\": {\n"
                + "            \"deleted\": {\n"
                + "              \"value\": \"ALLOWED\"\n"
                + "            }\n"
                + "          }\n"
                + "        },\n"
                + "        {\n"
                + "          \"exists\": {\n"
                + "            \"field\": \"fullText\"\n"
                + "          }\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"size\":0\n"
                + "}";

        String ABSTRACT_EXISTS_QUERY = "{\n"
                + "  \"query\": {\n"
                + "    \"bool\": {\n"
                + "      \"must\": [\n"
                + "        {\n"
                + "          \"term\": {\n"
                + "            \"deleted\": {\n"
                + "              \"value\": \"ALLOWED\"\n"
                + "            }\n"
                + "          }\n"
                + "        },\n"
                + "        {\n"
                + "          \"exists\": {\n"
                + "            \"field\": \"description\"\n"
                + "          }\n"
                + "        }\n"
                + "      ]\n"
                + "    }\n"
                + "  },\n"
                + "  \"size\":0\n"
                + "}";

        // * TOTAL
        Search totalSearch = new Search.Builder(TOTAL_QUERY)
                .addIndex("articles")
                .addType("article")
                .build();

        io.searchbox.core.SearchResult totalSearchResult;
        try {
            totalSearchResult = jestClient.execute(totalSearch);

            if (totalSearchResult != null) {
                String total = totalSearchResult.getJsonObject().getAsJsonObject("hits").get("total").getAsString();
                reportContent.setTotal(Integer.parseInt(total));
            }
        } catch (IOException ex) {
            Logger.getLogger(ReportingService.class.getName()).log(Level.SEVERE, null, ex);
        }

        // * Metadata only
        Search metadataSearch = new Search.Builder(METADATA_ONLY_QUERY)
                .addIndex("articles")
                .addType("article")
                .build();

        io.searchbox.core.SearchResult metadataSearchResult;
        try {
            metadataSearchResult = jestClient.execute(metadataSearch);

            if (metadataSearchResult != null) {
                String total = metadataSearchResult.getJsonObject().getAsJsonObject("hits").get("total").getAsString();
                reportContent.setMetadata_only(Integer.parseInt(total));
            }
        } catch (IOException ex) {
            Logger.getLogger(ReportingService.class.getName()).log(Level.SEVERE, null, ex);
        }

        // * Full text
        Search ftSearch = new Search.Builder(FULL_RECORDS_QUERY)
                .addIndex("articles")
                .addType("article")
                .build();

        io.searchbox.core.SearchResult ftSearchResult;
        try {
            ftSearchResult = jestClient.execute(ftSearch);

            if (ftSearchResult != null) {
                String total = ftSearchResult.getJsonObject().getAsJsonObject("hits").get("total").getAsString();
                reportContent.setFull_text_records(Integer.parseInt(total));
            }
        } catch (IOException ex) {
            Logger.getLogger(ReportingService.class.getName()).log(Level.SEVERE, null, ex);
        }

        // * With abstract
        Search abstractSearch = new Search.Builder(ABSTRACT_EXISTS_QUERY)
                .addIndex("articles")
                .addType("article")
                .build();

        io.searchbox.core.SearchResult abstractSearchResult;
        try {
            abstractSearchResult = jestClient.execute(abstractSearch);

            if (abstractSearchResult != null) {
                String total = abstractSearchResult.getJsonObject().getAsJsonObject("hits").get("total").getAsString();
                reportContent.setRecords_with_Abstract(Integer.parseInt(total));
            }
        } catch (IOException ex) {
            Logger.getLogger(ReportingService.class.getName()).log(Level.SEVERE, null, ex);
        }

        return reportContent;
    }

    public ReportUsers getUsersReport() {
        ReportUsers reportUsers = new ReportUsers();

        String API_USERS_COUNT_SQL = "SELECT COUNT(*) FROM api_keys where enabled=1";
        Integer apiUsersCount = jdbcTemplate.queryForObject(API_USERS_COUNT_SQL, Integer.class);
        reportUsers.setAPI_users(apiUsersCount);

        String DASHBOARD_COUNT_SQL = "SELECT COUNT(*) FROM DashboardRepo";
        Integer dashboardCount = jdbcTemplate.queryForObject(DASHBOARD_COUNT_SQL, Integer.class);
        reportUsers.setDashboard_Users(dashboardCount);

        String RECOMMENDER_USERS_COUNT_SQL = "SELECT COUNT(*) FROM recommender_user";
        Integer recommenderUsersCount = jdbcTemplate.queryForObject(RECOMMENDER_USERS_COUNT_SQL, Integer.class);
        reportUsers.setRecommender_users(recommenderUsersCount);

        String DATADUMP_COUNT_SQL = "SELECT COUNT(*) FROM data_dump_request";
        Integer datadumpCount = jdbcTemplate.queryForObject(DATADUMP_COUNT_SQL, Integer.class);
        reportUsers.setTotal_datadump_registrations(datadumpCount);

        return reportUsers;
    }

}
