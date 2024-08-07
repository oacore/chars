package uk.ac.core.itemclouduploader;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import java.io.File;
import javax.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import uk.ac.core.common.model.task.TaskDescription;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.common.model.task.parameters.SingleItemTaskParameters;
import uk.ac.core.database.service.document.RepositoryDocumentDAO;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.itemclouduploader.database.CloudPdfUrlDAO;
import uk.ac.core.singleitemworker.SingleItemWorker;

public class CloudUploaderWorker extends SingleItemWorker {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CloudUploaderWorker.class);

    @Autowired
    private FilesystemDAO filesystemDAO;

    @Autowired
    private RepositoryDocumentDAO repositoryDocumentDAO;

    @Autowired
    private CloudPdfUrlDAO cloudPdfUrlDAO;

    @Value("${aws_access_key}")
    private String awsAccessKey;
    @Value("${aws_secret_key}")
    private String awsSecretKey;
    @Value("${aws_bucket_name}")
    private String bucketName;
    @Value("${aws_region_name}")
    private String awsRegion;
    private AmazonS3 s3Client;

    @PostConstruct
    private void initS3() {
        BasicAWSCredentials creds = new BasicAWSCredentials(awsAccessKey, awsSecretKey);
        s3Client = AmazonS3ClientBuilder.standard().withRegion(awsRegion).withCredentials(new AWSStaticCredentialsProvider(creds)).build();

    }

    @Override
    public void taskReceived(Object task, @Header(AmqpHeaders.CHANNEL) Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) Long deliveryTag) {
        workerStatus.setChannel(channel);
        workerStatus.setDeliveryTag(deliveryTag);
        super.taskReceived(task, channel, deliveryTag);
    }

    @Override
    public TaskItemStatus process(TaskDescription taskDescription) {

        String params = taskDescription.getTaskParameters();
        SingleItemTaskParameters singleItemTaskParameters = new Gson().fromJson(params, SingleItemTaskParameters.class);
        final Integer articleId = singleItemTaskParameters.getArticle_id();
        final Integer repositoryId = repositoryDocumentDAO.getRepositoryDocumentById(articleId).getIdRepository();
        String pdfPath = this.filesystemDAO.getPdfPath(articleId, repositoryId);
        File file = new File(pdfPath);
        Boolean success = false;
        if (file.exists()) {
            String keyName = repositoryId + "/" + file.getName();
            try {
                PutObjectResult putObjectResult = s3Client.putObject(bucketName, keyName, file);
                success = !putObjectResult.getContentMd5().isEmpty();
                if (success) {
                    cloudPdfUrlDAO.save(articleId, "s3://" + bucketName + "/" + keyName);
                    filesystemDAO.moveFile(pdfPath, pdfPath + ".deleted");
                }
            } catch (AmazonServiceException e) {
                logger.error("Error in uplodaing to aws", e);
            }
        }
        TaskItemStatus taskItemStatus = new TaskItemStatus();
        taskItemStatus.setSuccess(success);
        taskItemStatus.setTaskId(taskDescription.getUniqueId());
        return taskItemStatus;
    }

}
