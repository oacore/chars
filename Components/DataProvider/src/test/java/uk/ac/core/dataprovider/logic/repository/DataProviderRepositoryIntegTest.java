package uk.ac.core.dataprovider.logic.repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.ac.core.dataprovider.logic.entity.DataProvider;
import uk.ac.core.dataprovider.logic.repository.dataprovider.DataProviderRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Integration tests for {@link DataProviderRepository}.
 */
@DataJpaTest
@ExtendWith(SpringExtension.class)
public class DataProviderRepositoryIntegTest {

    @Autowired
    private DataProviderRepository dataProviderRepository;

    private static final String FAKE_OAI_ENDPOINT_1 = "https://fake.oai";
    private static final String FAKE_OAI_ENDPOINT_2 = "https://fake1.oai";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final boolean JOURNAL = false;

    @Test
    public void shouldSave() {
        //given
        DataProvider initialEntity = createEntity();

        //when
        DataProvider result = dataProviderRepository.save(initialEntity);

        //then
        assertEquals(initialEntity, result);
    }

    private DataProvider createEntity() {
        DataProvider dataProvider = new DataProvider();
        dataProvider.setUrlOaipmh(FAKE_OAI_ENDPOINT_1);
        dataProvider.setName(NAME);
        dataProvider.setDescription(DESCRIPTION);
        dataProvider.setJournal(JOURNAL);
        return dataProvider;
    }

    @Test
    public void updateShouldOverwrite() {

        //given entity already exists
        DataProvider initialEntity = createEntity();
        dataProviderRepository.save(initialEntity);

        //When
        DataProvider put = createEntityForPut();

        DataProvider putResponse = dataProviderRepository.save(put);

        //Then
        assertEquals(FAKE_OAI_ENDPOINT_2, putResponse.getUrlOaipmh());
        assertNull(putResponse.getName());
        assertNull(putResponse.getDescription());
        assertFalse(putResponse.isJournal());
    }

    private DataProvider createEntityForPut() {
        DataProvider dataProvider = new DataProvider();
        dataProvider.setUrlOaipmh(FAKE_OAI_ENDPOINT_2);
        return dataProvider;
    }
}