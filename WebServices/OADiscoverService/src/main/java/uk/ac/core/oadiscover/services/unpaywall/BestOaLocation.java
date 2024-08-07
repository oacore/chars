
package com.example;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BestOaLocation {

    @SerializedName("evidence")
    @Expose
    private String evidence;
    @SerializedName("host_type")
    @Expose
    private String hostType;
    @SerializedName("is_best")
    @Expose
    private Boolean isBest;
    @SerializedName("license")
    @Expose
    private Object license;
    @SerializedName("pmh_id")
    @Expose
    private String pmhId;
    @SerializedName("updated")
    @Expose
    private String updated;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("url_for_landing_page")
    @Expose
    private String urlForLandingPage;
    @SerializedName("url_for_pdf")
    @Expose
    private String urlForPdf;
    @SerializedName("version")
    @Expose
    private String version;

    public String getEvidence() {
        return evidence;
    }

    public void setEvidence(String evidence) {
        this.evidence = evidence;
    }

    public String getHostType() {
        return hostType;
    }

    public void setHostType(String hostType) {
        this.hostType = hostType;
    }

    public Boolean getIsBest() {
        return isBest;
    }

    public void setIsBest(Boolean isBest) {
        this.isBest = isBest;
    }

    public Object getLicense() {
        return license;
    }

    public void setLicense(Object license) {
        this.license = license;
    }

    public String getPmhId() {
        return pmhId;
    }

    public void setPmhId(String pmhId) {
        this.pmhId = pmhId;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlForLandingPage() {
        return urlForLandingPage;
    }

    public void setUrlForLandingPage(String urlForLandingPage) {
        this.urlForLandingPage = urlForLandingPage;
    }

    public String getUrlForPdf() {
        return urlForPdf;
    }

    public void setUrlForPdf(String urlForPdf) {
        this.urlForPdf = urlForPdf;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "BestOaLocation{" + "evidence=" + evidence + ", hostType=" + hostType + ", isBest=" + isBest + ", license=" + license + ", pmhId=" + pmhId + ", updated=" + updated + ", url=" + url + ", urlForLandingPage=" + urlForLandingPage + ", urlForPdf=" + urlForPdf + ", version=" + version + '}';
    }

    
    
}
