package uk.ac.core.common.util.downloader;

/**
 *
 * @author Drahomira Herrmannova <d.herrmannova@gmail.com>
 */
public class DownloadResult {

    private int statusCode = 0;
    private String contentType = null;
    private byte[] content = null;
    private String baseUrl;

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

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

}
