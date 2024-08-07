package uk.ac.core.datawarehouse.report.worker;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import uk.ac.core.cloud.client.CloudException;
import uk.ac.core.cloud.client.S3UploadService;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItem;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.TaskType;
import uk.ac.core.datawarehouse.report.worker.model.*;
import uk.ac.core.datawarehouse.report.worker.services.ReportingService;
import uk.ac.core.datawarehouse.report.worker.writer.DataCSVWriter;
import uk.ac.core.slack.client.SlackWebhookService;
import uk.ac.core.worker.ScheduledWorker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author lucas
 */
@Service
@PropertySource("file:/data/core-properties/jisc-warehouse-${spring.profiles.active}.properties")
public class DataWarehouseReportWorker extends ScheduledWorker {

    private TaskType taskType = TaskType.WAREHOUSE_REPORT_GENERATION;

    private final static String REPORTS_BASE_DIRECTORY = DataWarehouseConfiguration.REPORTS_BASE_DIRECTORY;
    private static final Logger LOG = Logger.getLogger("DataWarehouseReportWorker");

    private boolean UploadToJisc = true;

    @Value("${jisc_warehouse.aws_access_key_prod}")
    public String AWS_ACCESS_KEY_PROD;
    @Value("${jisc_warehouse.aws_access_key_stagin}")
    public String AWS_ACCESS_KEY_STAGIN;
    @Value("${jisc_warehouse.aws_secret_key_prod}")
    public String AWS_SECRET_KEY_PROD;
    @Value("${jisc_warehouse.aws_secret_key_stagin}")
    public String AWS_SECRET_KEY_STAGIN;
    @Value("${jisc_warehouse.aws_bucket_prod}")
    public String AWS_BUCKET_PROD;
    @Value("${jisc_warehouse.aws_bucket_stagin}")
    public String AWS_BUCKET_STAGIN;

    @Autowired
    ReportingService reportingService;

    @Autowired
    S3UploadService s3UploadService;


    @Override
    public String generateReport(List<TaskItemStatus> results, boolean taskOverallSuccess) {

        LocalDate localDate = LocalDate.now();
        String today = DateTimeFormatter.ofPattern("yyy_MM_dd").format(localDate);

        String message = "<h1>Jisc data Warehouse report generation</h1><br>" +
                "<b><span color='orange'>The links will last for only 12 hours.</span></b>";
        for (TaskItemStatus taskItemStatus : results) {
            message += generateReportPerItem((WarehouseReportTaskItemStatus) taskItemStatus);
        }
        message += "<br><br> Love <br> Cron Scheduler";
        return message;
    }

    private String generateReportPerItem(WarehouseReportTaskItemStatus warehouseReportTaskItemStatus) {
        String message = "";
        if (!warehouseReportTaskItemStatus.isSuccess()) {
            message = String.format("<b><span color='red'>Error - The report for %s failed for the following reason: %s.",
                    warehouseReportTaskItemStatus.getReportType(), warehouseReportTaskItemStatus.getMessageOnError()
            );
        }
        DecimalFormat decimalFormat = new DecimalFormat("###,###.###");
        switch (warehouseReportTaskItemStatus.getReportType()) {
            case CONTENT:
                ReportContent reportContent = (ReportContent) warehouseReportTaskItemStatus.getReport();
                message = String.format("<h2>Content report (took %s seconds to generate)</h2>" +
                                "<a href='%s'>Download the file</a>" +
                                "<ul>" +
                                "<li>Total: %s</li>" +
                                "<li>#metadata only: %s</li>" +
                                "<li>#metadata Only with Abstracts: %s</li>" +
                                "<li>#full text records: %s</li>" +
                                "</ul> ",
                        warehouseReportTaskItemStatus.getDuration(),
                        warehouseReportTaskItemStatus.getUploadStatus().getCloudLink(),
                        decimalFormat.format(reportContent.getTotal()),
                        decimalFormat.format(reportContent.getMetadata_only()),
                        decimalFormat.format(reportContent.getRecords_with_Abstract()),
                        decimalFormat.format(reportContent.getFull_text_records()));
                break;
            case DATA_PROVIDERS:

                ReportDataProviders reportDataProviders = (ReportDataProviders) warehouseReportTaskItemStatus.getReport();
                message = String.format("<h2>Data provider report (took %s seconds to generate)</h2>" +
                                "<a href='%s'>Download the file</a>" +
                                "%s data providers exported. <br>",
                        warehouseReportTaskItemStatus.getDuration(),
                        warehouseReportTaskItemStatus.getUploadStatus().getCloudLink(),
                        reportDataProviders.getDataProvidersList().size());
                break;
            case DATA_PROVIDERS_AGGREGATION:
                ReportDataProviderAggregation reportDataProviderAggregation = (ReportDataProviderAggregation) warehouseReportTaskItemStatus.getReport();
                message = String.format("<h2>Data provider aggregation report (took %s seconds to generate)</h2>" +
                                "<a href='%s'>Download the file</a>" +
                                "<ul>" +
                                "<li>Total number of data providers: %s</li>" +
                                "<li>#UK data providers: %s</li>" +
                                "<li>#rest of the world: %s</li>" +
                                "<li>Publisher connector providers: %s</li>" +
                                "</ul>" +
                                "<h3>Data provider by continent</h3> " +
                                "<ul>" +
                                "<li>Africa: %s</li>" +
                                "<li>Asia: %s</li>" +
                                "<li>Europe: %s</li>" +
                                "<li>North America: %s</li>" +
                                "<li>South America: %s</li>" +
                                "<li>Oceania: %s</li>" +
                                "</ul> ",
                        warehouseReportTaskItemStatus.getDuration(),
                        warehouseReportTaskItemStatus.getUploadStatus().getCloudLink(),
                        decimalFormat.format(reportDataProviderAggregation.getCount_of_active_data_providers()),
                        decimalFormat.format(reportDataProviderAggregation.getCount_of_UK_repositories()),
                        decimalFormat.format(reportDataProviderAggregation.getCount_of_Rest_of_World_repositories()),
                        decimalFormat.format(reportDataProviderAggregation.getCount_of_publisher_connector_providers()),
                        decimalFormat.format(reportDataProviderAggregation.getCount_of_active_Africa_repositories()),
                        decimalFormat.format(reportDataProviderAggregation.getCount_of_active_Asia_repositories()),
                        decimalFormat.format(reportDataProviderAggregation.getCount_of_active_Europe_repositories()),
                        decimalFormat.format(reportDataProviderAggregation.getCount_of_active_North_America_repositories()),
                        decimalFormat.format(reportDataProviderAggregation.getCount_of_active_South_America_repositories()),
                        decimalFormat.format(reportDataProviderAggregation.getCount_of_active_Oceania_repositories())
                );
                break;
            case USERS:
                ReportUsers reportUsers = (ReportUsers) warehouseReportTaskItemStatus.getReport();
                message = String.format("<h2>User report (took %s seconds to generate)</h2>" +
                                "<a href='%s'>Download the file</a>" +
                                "<ul>" +
                                "<li>API Users: %s</li>" +
                                "<li>Dashboard users: %s</li>" +
                                "<li>Recommender Users: %s</li>" +
                                "<li>Datadump registrations: %s</li>" +
                                "</ul> ",
                        warehouseReportTaskItemStatus.getDuration(),
                        warehouseReportTaskItemStatus.getUploadStatus().getCloudLink(),
                        decimalFormat.format(reportUsers.getAPI_users()),
                        decimalFormat.format(reportUsers.getDashboard_Users()),
                        decimalFormat.format(reportUsers.getRecommender_users()),
                        decimalFormat.format(reportUsers.getTotal_datadump_registrations()));
                break;
        }

        return message;
    }


    public String generatePreviewReport() {
        LocalDate localDate = LocalDate.now();
        String today = DateTimeFormatter.ofPattern("yyy_MM_dd").format(localDate);

        return "A dry run of the Jisc Data Warehouse data is generated but has not been uploaded"
                + "<br/>"
                + "A copy is located at "
                + "" + REPORTS_BASE_DIRECTORY + today
                + "<br/>"
                + "<br/>"
                + "Download the reports from:<br/>"
                + "<a href=\"http://core-appsvr01.open.ac.uk:8073/datawarehouse/" + today + "/content.csv\">content.csv</a><br/>"
                + "<a href=\"http://core-appsvr01.open.ac.uk:8073/datawarehouse/" + today + "/repositories_list.csv\">repositories_list</a><br/>"
                + "<a href=\"http://core-appsvr01.open.ac.uk:8073/datawarehouse/" + today + "/data_providers_aggregations.csv\">data_providers_aggregations.csv</a><br/>"
                + "<a href=\"http://core-appsvr01.open.ac.uk:8073/datawarehouse/" + today + "/users.csv\">users.csv</a><br/>"
                + "<br/>";
    }

    //execute at 25th of every month
    @Scheduled(cron = "0 0 0 25 * ?")
    public void previewReport() {
        UploadToJisc = false;

        List<TaskItem> dataToProcess = this.collectData();
        List<TaskItemStatus> results = this.process(dataToProcess);

        String mailMessage = this.generatePreviewReport();
        this.sendNotification(mailMessage);
    }

    @Override
    public TaskType getTaskType() {
        return TaskType.WAREHOUSE_REPORT_GENERATION;
    }

    @Override
    @Scheduled(cron = "0 0 0 1 * ?")// every 1st of month
    public void scheduledStart() {
        UploadToJisc = true;
        this.start();
    }

    @Override
    public TaskDescription generateTaskDescription() {
        TaskDescription task = new TaskDescription();
        task.setCreationTime(System.currentTimeMillis());
        task.setStartTime(System.currentTimeMillis());
        task.setType(TaskType.WAREHOUSE_REPORT_GENERATION);
        return task;
    }

    @Override
    public List<TaskItem> collectData() {

        List<TaskItem> taskItems = new ArrayList<>();

        WarehouseReportItem contentItem = new WarehouseReportItem();
        contentItem.setFilename("content.csv");
        contentItem.setReportType(ReportType.CONTENT);
        taskItems.add(contentItem);

        WarehouseReportItem usersItem = new WarehouseReportItem();
        usersItem.setFilename("users.csv");
        usersItem.setReportType(ReportType.USERS);
        taskItems.add(usersItem);

        WarehouseReportItem dataProvidersAggregationsItem = new WarehouseReportItem();
        dataProvidersAggregationsItem.setFilename("data_providers_aggregations.csv");
        dataProvidersAggregationsItem.setReportType(ReportType.DATA_PROVIDERS_AGGREGATION);
        taskItems.add(dataProvidersAggregationsItem);

        WarehouseReportItem repositoriesListItem = new WarehouseReportItem();
        repositoriesListItem.setFilename("repositories_list.csv");
        repositoriesListItem.setReportType(ReportType.DATA_PROVIDERS);
        taskItems.add(repositoriesListItem);

        return taskItems;
    }

    private Report performReport(ReportType type, File file) throws IOException {
        PrintWriter writer = new PrintWriter(file);
        Report report = null;
        switch (type) {
            case CONTENT:
                ReportContent reportContent = reportingService.getContentReport();
                DataCSVWriter.writeContentData(writer, reportContent);
                report = reportContent;
                break;
            case DATA_PROVIDERS:
                ReportDataProviders reportDataProviders = reportingService.getRepositoriesList();
                DataCSVWriter.writeRepositoryData(writer, reportDataProviders);
                report = reportDataProviders;

                break;
            case DATA_PROVIDERS_AGGREGATION:
                ReportDataProviderAggregation reportDataProviderAggregation = reportingService.getReportDataProviderAggregation();
                DataCSVWriter.writeReportDataProviderAggregation(writer, reportDataProviderAggregation);
                report = reportDataProviderAggregation;

                break;
            case USERS:
                ReportUsers reportUsers = reportingService.getUsersReport();
                DataCSVWriter.writeUsersData(writer, reportUsers);
                report = reportUsers;
                break;
        }
        return report;
    }

    @Override
    public void collectStatistics(List<TaskItemStatus> results) {
        // emm ??
    }

    @Override
    public List<TaskItemStatus> process(List<TaskItem> taskItems) {
        List<TaskItemStatus> statuses = new ArrayList<>();

        // create folder to store today's reports
        LocalDate localDate = LocalDate.now();
        String today = DateTimeFormatter.ofPattern("yyy_MM_dd").format(localDate);

        String directoryOfToday = REPORTS_BASE_DIRECTORY + today;
        File dir = new File(directoryOfToday);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        List<File> filesToUpload = new ArrayList<>();
        for (TaskItem item : taskItems) {
            // download and store in (today's) folder
            WarehouseReportItem weItem = (WarehouseReportItem) item;
            File file = new File(directoryOfToday + "/" + weItem.getFilename());
            WarehouseReportTaskItemStatus warehouseReportTaskItemStatus = new WarehouseReportTaskItemStatus();
            warehouseReportTaskItemStatus.setReportType(weItem.getReportType());
            try {
                LocalDateTime start = LocalDateTime.now();
                Report report = this.performReport(weItem.getReportType(), file);
                warehouseReportTaskItemStatus.setReport(report);
                Long duration = ChronoUnit.SECONDS.between(start, LocalDateTime.now());
                if (file.exists() && file.length() > 10) {
                    warehouseReportTaskItemStatus.setProcessedCount(1);
                    warehouseReportTaskItemStatus.setSuccess(true);
                    warehouseReportTaskItemStatus.setSuccessfulCount(1);
                    warehouseReportTaskItemStatus.setTaskId("file_download_at_" + file.getAbsolutePath());// not a UUID but good enough for this purpose
                    warehouseReportTaskItemStatus.setNumberOfItemsToProcess(1);
                    warehouseReportTaskItemStatus.setDuration(duration);
                    UploadStatus uploadStatus = this.uploadToS3(file);
                    if (uploadStatus.isSuccess()) {
                        warehouseReportTaskItemStatus.setSuccess(true);
                    } else {
                        warehouseReportTaskItemStatus.setSuccess(false);
                    }
                    warehouseReportTaskItemStatus.setUploadStatus(uploadStatus);
                }
            } catch (FileNotFoundException e) {
                LOG.log(Level.SEVERE, "File cannot be created", e);
                warehouseReportTaskItemStatus.setSuccess(false);
                warehouseReportTaskItemStatus.setMessageOnError(e.getMessage());
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Error writing the file", e);
                warehouseReportTaskItemStatus.setSuccess(false);
                warehouseReportTaskItemStatus.setMessageOnError(e.getMessage());
            } catch (CloudException e) {
                LOG.log(Level.SEVERE, "Error uploading to the cloud", e);
                warehouseReportTaskItemStatus.setSuccess(false);
                warehouseReportTaskItemStatus.setMessageOnError(e.getMessage());
            }
            statuses.add(warehouseReportTaskItemStatus);
        }
        return statuses;
    }

    private UploadStatus uploadToS3(File file) throws CloudException {

        //
        // production
        //
        UploadStatus uploadStatus = new UploadStatus();
        s3UploadService.setCredentials(AWS_ACCESS_KEY_PROD,
                AWS_SECRET_KEY_PROD,
                "eu-west-1");
        LOG.log(Level.INFO, String.format("Uploading file %s to bucket %s", file.getAbsolutePath(), AWS_BUCKET_PROD));
        s3UploadService.uploadFile(AWS_BUCKET_PROD, file.getName(), file);
        uploadStatus.setCloudLink(generateCloudLink(file.getName()));
        return uploadStatus;
    }

    private String generateCloudLink(String filename) {
        LocalDateTime expiration =LocalDateTime.now();
        expiration=expiration.plusHours(12);


        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(AWS_BUCKET_PROD, filename)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(java.sql.Timestamp.valueOf(expiration));
        URL url = s3UploadService.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toExternalForm();
    }

    private void uploadToS3(List<File> filesToUpload) {
        // there are two buckets - prod and stagin

        //
        // staging
        //
        s3UploadService.setCredentials(AWS_ACCESS_KEY_STAGIN,
                AWS_SECRET_KEY_STAGIN,
                "eu-west-1");

        for (File file : filesToUpload) {
            try {
                LOG.log(Level.INFO, String.format("Uploading file %s to bucket %s", file.getAbsolutePath(), AWS_ACCESS_KEY_STAGIN));
                s3UploadService.uploadFile(AWS_BUCKET_STAGIN, file.getName(), file);
            } catch (CloudException ce) {
                LOG.log(Level.SEVERE, "Cannot upload file : " + file.getName() + " to S3 staging bucket", file);
            }
        }

        //
        // production
        //
        s3UploadService.setCredentials(AWS_ACCESS_KEY_PROD,
                AWS_SECRET_KEY_PROD,
                "eu-west-1");

        for (File file : filesToUpload) {
            try {
                LOG.log(Level.INFO, String.format("Uploading file %s to bucket %s", file.getAbsolutePath(), AWS_BUCKET_PROD));
                s3UploadService.uploadFile(AWS_BUCKET_PROD, file.getName(), file);
            } catch (CloudException ce) {
                LOG.log(Level.SEVERE, "Cannot upload file : " + file.getName() + " to S3 production bucket", file);
            }
        }

    }

    @Override
    public void sendNotification(String messageBody) {
        //Still send the Email
        super.sendNotification(messageBody);

        System.out.println("Sending slack message!");

        SlackWebhookService.sendMessage(messageBody, "operations-report");

    }

}
