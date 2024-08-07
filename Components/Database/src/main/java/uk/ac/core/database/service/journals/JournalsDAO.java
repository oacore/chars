package uk.ac.core.database.service.journals;

import uk.ac.core.database.model.JournalISSN;

import java.util.List;

/**
 *
 * @author lucasanastasiou
 */
public interface JournalsDAO {

    String getJournalTitleByIdentifier(String journalIdentifier);

    void saveAll(List<JournalISSN> publisherNames);

    JournalISSN findByIdentifier(String identifier);

    List<JournalISSN> findAllIssns();
}
