package uk.ac.core.cloud.client;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author lucas
 */
@Service
public class S3UploadService implements UploadService {

    private AmazonS3 s3Client;

    private static final Logger LOG = Logger.getLogger(S3UploadService.class.getName());

    @Override
    public void uploadFile(String bucketName, String keyName, File file) throws CloudException {

        if (file.exists()) {
            try {
                PutObjectResult putObjectResult = s3Client.putObject(bucketName, keyName, file);
                boolean success = !putObjectResult.getContentMd5().isEmpty();
                if (!success) {
                    throw new CloudException(String.format("File hash is empty, assuming that upload of %s has failed", file.getName()));
                }
            } catch (AmazonServiceException e) {
                LOG.log(Level.SEVERE, "Error in uplodaing to aws", e);
                throw new CloudException(e.getMessage(), e);
            }
        } else {
            throw new CloudException(String.format("File %s does not exist!", file.getName()));
        }

    }

    @Override
    public void setCredentials(String awsAccessKey, String awsSecretKey, String awsRegion) {
        BasicAWSCredentials awsCreds = new BasicAWSCredentials(awsAccessKey, awsSecretKey);

        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCreds))
                .withRegion(awsRegion)
                .build();

    }

    public URL generatePresignedUrl(GeneratePresignedUrlRequest generatePresignedUrlRequest) {
        return s3Client.generatePresignedUrl(generatePresignedUrlRequest);
    }
}
