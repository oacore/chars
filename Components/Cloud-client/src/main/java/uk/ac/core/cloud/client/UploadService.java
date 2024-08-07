package uk.ac.core.cloud.client;

import java.io.File;

/**
 *
 * @author lucas
 */
public interface UploadService {

    public void uploadFile(String bucketName, String keyName, File f)  throws CloudException;
    
    public void setCredentials(String awsAccessKey, String awsSecretKey, String awsRegion);
}
