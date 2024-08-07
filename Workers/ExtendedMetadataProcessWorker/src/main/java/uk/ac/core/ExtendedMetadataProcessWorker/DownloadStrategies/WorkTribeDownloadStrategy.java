package uk.ac.core.ExtendedMetadataProcessWorker.DownloadStrategies;

import org.springframework.stereotype.Service;
import uk.ac.core.dataprovider.logic.entity.DataProvider;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

@Service
public class WorkTribeDownloadStrategy extends EprintsDownloadStrategy {

    @Override
    public boolean isCompatible(DataProvider dataProvider) {
        this.setDataProvider(dataProvider);
        return dataProvider.getSoftware().toLowerCase().contains("worktribe");
    }

    @Override
    protected URI composeUrlToTry(String protocol, String[] parts) throws URISyntaxException, MalformedURLException {
        return new URL(protocol, parts[1], "/output/" + parts[2]).toURI();
    }
}
