package uk.ac.core.database.service.publishername;

import uk.ac.core.database.model.PublisherName;

import java.util.List;
import java.util.Optional;

public interface PublisherNameDAO {

    void save(PublisherName entity);

    void saveAll(List<PublisherName> publisherNames);

    Optional<PublisherName> findByPrefix(String prefix);

    Optional<PublisherName> findByName(String name);

    List<PublisherName> findAll();
}
