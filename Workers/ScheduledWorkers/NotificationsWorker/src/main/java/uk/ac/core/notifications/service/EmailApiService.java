package uk.ac.core.notifications.service;

import uk.ac.core.notifications.exceptions.NoDataForEmailException;
import uk.ac.core.notifications.model.DeduplicationData;
import uk.ac.core.notifications.model.HarvestingData;

public interface EmailApiService {
    HarvestingData getHarvestingData(int repoId) throws NoDataForEmailException;
    DeduplicationData getDuplicatesData(int repoId) throws NoDataForEmailException;
}
