package uk.ac.core.datawarehouse.report.worker.model;

import java.util.List;

public class ReportDataProviders extends Report {
    List<ReportRepository> dataProvidersList;

    public List<ReportRepository> getDataProvidersList() {
        return dataProvidersList;
    }

    public void setDataProvidersList(List<ReportRepository> dataProvidersList) {
        this.dataProvidersList = dataProvidersList;
    }
}
