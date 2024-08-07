package uk.ac.core.common.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Http response from Apache Html Client.
 */
public class HttpResponseDTO {

    private int statusCode;

    private String contentType;

    private String encoding;

    private String responseBody;

    private String uri;

    public HttpResponseDTO(int statusCode, String contentType, String encoding, String responseBody, String uri) {
        this.statusCode = statusCode;
        this.contentType = contentType;
        this.encoding = encoding;
        this.responseBody = responseBody;
        this.uri = uri;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isSuccessful() {
        return statusCode == HttpStatus.OK.value();
    }

    public boolean isXmlResponse() {
        return getContentType().equals(MediaType.APPLICATION_XML_VALUE) ||
                getContentType().equals(MediaType.TEXT_XML_VALUE);
    }

    public boolean is5xxStatusCode() {
        return getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR.value() ||
                getStatusCode() == HttpStatus.BAD_GATEWAY.value() ||
                getStatusCode() == HttpStatus.GATEWAY_TIMEOUT.value() ||
                getStatusCode() == HttpStatus.SERVICE_UNAVAILABLE.value();
    }
}