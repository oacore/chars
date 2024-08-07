package uk.ac.core.rioxxvalidation.rioxx;

public abstract class ValidationReportAbstract {

    protected String rioxxVersion="NA";

    public String getRioxxVersion() {
        return rioxxVersion;
    }

    public void setRioxxVersion(String rioxxVersion) {
        this.rioxxVersion = rioxxVersion;
    }
}
