package uk.ac.core.dataprovider.logic.converter;

import uk.ac.core.dataprovider.logic.dto.BaseDataProviderBO;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;
import uk.ac.core.dataprovider.logic.dto.OpenDoarDataProviderBO;
import uk.ac.core.dataprovider.logic.entity.DataProvider;
import uk.ac.core.dataprovider.logic.entity.DataProviderLocation;

import java.util.Date;
import java.sql.Timestamp;

public final class DataProviderConverter {

    private DataProviderConverter() {

    }

    public static DataProvider toDataProvider(DataProviderBO dataProviderBO) {
        DataProvider dataProvider = new DataProvider();
        if (dataProviderBO.getId() != null) {
            dataProvider.setId(dataProviderBO.getId());
        }
        dataProvider.setUrlOaipmh(dataProviderBO.getOaiPmhEndpoint());
        dataProvider.setName(dataProviderBO.getName());
        dataProvider.setDescription(dataProviderBO.getDescription());
        dataProvider.setJournal(dataProviderBO.isJournal());
        dataProvider.setUrlHomepage(dataProviderBO.getHomepage());
//        dataProvider.setMetadataFormat(dataProviderBO.getMetadataFormat());
        if (dataProviderBO.getMetadataFormat() != null) {
            dataProvider.setMetadataFormat(dataProviderBO.getMetadataFormat());
        }
        dataProvider.setSoftware(dataProviderBO.getSoftware());
        dataProvider.setSource(dataProviderBO.getSource());
        dataProvider.setUri(dataProviderBO.getOaiPmhEndpoint());
        dataProvider.setDisabled(dataProviderBO.isDisabled());
        if (dataProviderBO.getCreatedAt() != null) {
            dataProvider.setCreated_date(Timestamp.valueOf(dataProviderBO.getCreatedAt()));
        } else {
            dataProvider.setCreated_date(new Date());
        }
        if (dataProviderBO instanceof BaseDataProviderBO) {
            dataProvider.setBaseId(((BaseDataProviderBO) dataProviderBO).getBaseId());
        }
        if (dataProviderBO instanceof OpenDoarDataProviderBO) {
            dataProvider.setOpenDoarId(((OpenDoarDataProviderBO) dataProviderBO).getOpenDoarId());
        }
        return dataProvider;
    }

    public static DataProviderLocation toDataProviderLocation(DataProviderBO dataProviderBO) {
        DataProviderLocation dataProviderLocation = new DataProviderLocation();
        if (dataProviderBO.getCountryCode() != null && dataProviderBO.getCountryCode().trim().length() <= 2) {
            dataProviderLocation.setCountryCode(dataProviderBO.getCountryCode().trim());
        } else {
            dataProviderLocation.setCountryCode(null);
        }
        dataProviderLocation.setLatitude(dataProviderBO.getLatitude());
        dataProviderLocation.setLongitude(dataProviderBO.getLongitude());
        return dataProviderLocation;
    }

    public static DataProviderBO toCompactDataProviderBO(DataProvider dataProvider) {
        DataProviderBO dataProviderBO = new DataProviderBO();
        dataProviderBO.setId(dataProvider.getId());
        dataProviderBO.setOaiPmhEndpoint(dataProvider.getUrlOaipmh());
        dataProviderBO.setName(dataProvider.getName());
        dataProviderBO.setDescription(dataProvider.getDescription());
        dataProviderBO.setJournal(dataProvider.isJournal());
        dataProviderBO.setSoftware(dataProvider.getSoftware());
        dataProviderBO.setMetadataFormat(dataProvider.getMetadataFormat());
        if (dataProvider.getCreated_date() != null) {
            dataProviderBO.setCreatedAt(new Timestamp(dataProvider.getCreated_date().getTime()).toLocalDateTime());
        }
        return dataProviderBO;
    }

    public static DataProviderBO toCompactDataProviderBOWithActiveStatus(DataProvider dataProvider) {
        DataProviderBO dataProviderBO = toCompactDataProviderBO(dataProvider);
        dataProviderBO.setEnabled(!dataProvider.getDisabled());
        return dataProviderBO;
    }

    public static DataProviderBO toCompleteDataProviderBO(DataProvider dataProvider, DataProviderLocation dataProviderLocation) {
        DataProviderBO dataProviderBO = toCompactDataProviderBOWithActiveStatus(dataProvider);
        dataProviderBO.setCountryCode(dataProviderLocation.getCountryCode());
        dataProviderBO.setLongitude(dataProviderLocation.getLongitude());
        dataProviderBO.setLatitude(dataProviderLocation.getLatitude());
        return dataProviderBO;
    }
}
