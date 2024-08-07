package uk.ac.core.datawarehouse.report.worker.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.client.JestClient;
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
import uk.ac.core.datawarehouse.report.worker.model.ReportDataProviderAggregation;
import uk.ac.core.datawarehouse.report.worker.model.ReportRepository;

/**
 *
 * @author lucas
 */
@Service
public class RepositoriesReportingService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    JestClient jestClient;

    private static final Logger LOG = Logger.getLogger("RepositoriesReportingService");

    public List<ReportRepository> getRepositoriesList() {

        String SQL = "SELECT re.id_repository AS core_id,"
                + "re.id_opendoar AS id_opendoar,"
                + "re.id_roar AS id_roar,"
                + "re.name AS name, "
                + "rcj.id_jisc AS id_jisc,"
                + "rl.country_code AS country_code "
                + "FROM repository re LEFT JOIN repository_core_jisc rcj ON (re.id_repository=rcj.id_repository) "
                + "INNER JOIN repository_location rl ON (re.id_repository=rl.id_repository)";

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
                repository.setCount_metadata(mCount!=null?mCount:0);
                repository.setCount_fulltext(ftCount!=null?ftCount:0);

                repository.setRecommender_registered(Boolean.TRUE);
                repository.setRepository_dahboard_registered(Boolean.TRUE);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOG.log(Level.ALL, e.getMessage());
        }
        return allReposInDb;
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
            Map<Integer,Integer> results = new HashMap<>();

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
            Map<Integer,Integer> results = new HashMap<>();

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

        String SQL = "";
        return  null;

    }

}
