package uk.ac.core.documentdownload.downloader;

import org.apache.tika.metadata.Metadata;

/**
 *
 * @author Drahomira Herrmannova <d.herrmannova@gmail.com>
 */
public class DownloadResult {

    private int statusCode = 0;
    private String contentType = null;
    private long contentSize = 0;
    private byte[] contentFirstBytes = null;
    private String baseUrl;
    private String filePath;
    private Metadata headers = new Metadata();

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getContentSize() {
        return contentSize;
    }

    public void setContentSize(long contentSize) {
        this.contentSize = contentSize;
    }

    public byte[] getContentFirstBytes() {
        return contentFirstBytes;
    }

    public void setContentFirstBytes(byte[] contentFirstBytes) {
        this.contentFirstBytes = contentFirstBytes;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Metadata getHeaders() {
        return headers;
    }

    public void setHeaders(Metadata headers) {
        this.headers = headers;
    }
    
}
