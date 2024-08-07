package uk.ac.core.datawarehouse.report.worker.writer;

import com.opencsv.CSVWriter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import uk.ac.core.datawarehouse.report.worker.model.*;

/**
 * @author lucas
 */
public class DataCSVWriter {

    private static final Logger LOG = Logger.getLogger("DataCSVWriter");

    public static void writeContentData(PrintWriter writer, ReportContent reportContent) throws IOException {
        String[] CSV_HEADER = {
                "total",
                "metadata_only",
                "full_text_records",
                "records_with_Abstract"
        };
        CSVWriter csvWriter = new CSVWriter(writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
        csvWriter.writeNext(CSV_HEADER);
        String[] data = {
                reportContent.getTotal().toString(),
                reportContent.getMetadata_only().toString(),
                reportContent.getFull_text_records().toString(),
                reportContent.getRecords_with_Abstract().toString()
        };

        csvWriter.writeNext(data);
        csvWriter.flush();
        csvWriter.close();
    }

    public static void writeUsersData(PrintWriter writer, ReportUsers reportUsers) throws IOException {
        String[] CSV_HEADER = {
                "aPI_users",
                "dashboard_Users",
                "recommender_users",
                "total_datadump_registrations"
        };
        CSVWriter csvWriter = new CSVWriter(writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
        csvWriter.writeNext(CSV_HEADER);
        String[] data = {
                reportUsers.getAPI_users().toString(),
                reportUsers.getDashboard_Users().toString(),
                reportUsers.getRecommender_users().toString(),
                reportUsers.getTotal_datadump_registrations().toString()
        };

        csvWriter.writeNext(data);
        csvWriter.flush();
        csvWriter.close();
    }

    public static void writeRepositoryData(PrintWriter writer, ReportDataProviders reportDataProviders) throws IOException {

        List<ReportRepository> repositories = reportDataProviders.getDataProvidersList();
        String[] CSV_HEADER = {"CORE_ID",
                "Jisc_ID",
                "OpenDOAR_ID",
//            "ROAR_ID",
                "Name",
                "Institution",
                "Country",
                "rioxx",
                "count_metadata",
                "count_fulltext",
                "repository_core_inclusion_date",
                "repository_dahboard_registered",
                "recommender_registered"
        };
        CSVWriter csvWriter = new CSVWriter(writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
        csvWriter.writeNext(CSV_HEADER);

        for (ReportRepository repository : repositories) {
            String[] data = {
                    repository.getCORE_ID().toString(),
                    repository.getJisc_ID().toString(),
                    repository.getOpenDOAR_ID().toString(),
//                repository.getROAR_ID().toString(),
                    repository.getName(),
                    repository.getInstitution(),
                    repository.getCountry(),
                    repository.getRioxx_enabled().toString(),
                    repository.getCount_metadata().toString(),
                    repository.getCount_fulltext().toString(),
                    repository.getRepository_core_inclusion_date(),
                    repository.getRepository_dahboard_registered().toString(),
                    repository.getRecommender_registered().toString()
            };

            csvWriter.writeNext(data);
        }
        csvWriter.flush();
        csvWriter.close();

    }

    public static void writeReportDataProviderAggregation(PrintWriter writer, ReportDataProviderAggregation reportDataProviderAggregation) throws IOException {
        String[] CSV_HEADER = {
                "count_of_data_providers",
                "count_of_active_data_providers",
                "Count_of_publisher_connector_providers",
                "Count_of_UK_repositories",
                "Count_of_Rest_of_World_repositories",
                "Count_of_active_Europe_repositories",
                "Count_of_active_North_America_repositories",
                "Count_of_active_South_America_repositories",
                "Count_of_active_Asia_repositories",
                "Count_of_active_Oceania_repositories",
                "Count_of_active_Africa_repositories",
                "Count_of_active_location_uknown_repositories"
        };
        CSVWriter csvWriter = new CSVWriter(writer,
                CSVWriter.DEFAULT_SEPARATOR,
                CSVWriter.DEFAULT_QUOTE_CHARACTER,
                CSVWriter.DEFAULT_ESCAPE_CHARACTER,
                CSVWriter.DEFAULT_LINE_END);
        csvWriter.writeNext(CSV_HEADER);

        String[] data = {
                reportDataProviderAggregation.getCount_of_data_providers().toString(),
                reportDataProviderAggregation.getCount_of_active_data_providers().toString(),
                reportDataProviderAggregation.getCount_of_publisher_connector_providers().toString(),
                reportDataProviderAggregation.getCount_of_UK_repositories().toString(),
                reportDataProviderAggregation.getCount_of_Rest_of_World_repositories().toString(),
                reportDataProviderAggregation.getCount_of_active_Europe_repositories().toString(),
                reportDataProviderAggregation.getCount_of_active_North_America_repositories().toString(),
                reportDataProviderAggregation.getCount_of_active_South_America_repositories().toString(),
                reportDataProviderAggregation.getCount_of_active_Asia_repositories().toString(),
                reportDataProviderAggregation.getCount_of_active_Oceania_repositories().toString(),
                reportDataProviderAggregation.getCount_of_active_Africa_repositories().toString(),
                reportDataProviderAggregation.getCount_of_active_location_uknown_repositories().toString()
        };

        csvWriter.writeNext(data);
        csvWriter.flush();
        csvWriter.close();

    }

}
