package uk.ac.core.dataprovider.api.service;

import org.junit.Test;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;

public class DuplicatesServiceTest {

    private DuplicatesService serviceUnderTest = new DuplicatesService();

    @Test
    public void testCalculateConfidence() {
        double confidence = serviceUnderTest.calculateConfidence(2022, 2022,
                singletonList("John Dou"), singletonList("John Dou"));
        assertEquals(1d, confidence, 0.00001d);

        confidence = serviceUnderTest.calculateConfidence(2021, 2022,
                singletonList("John Dou"), singletonList("John Dou"));
        assertEquals(0.83d, confidence, 0.00001d);

        confidence = serviceUnderTest.calculateConfidence(2021, 2023,
                singletonList("John Dou"), singletonList("John Dou"));
        assertEquals(0.66d, confidence, 0.00001d);

        confidence = serviceUnderTest.calculateConfidence(2021, 2021,
                singletonList("John Dou"), singletonList("John Tou"));
        assertEquals(0.91d, confidence, 0.00001d);

        confidence = serviceUnderTest.calculateConfidence(2021, 2021,
                singletonList("John Dou, Alla Smith"), singletonList("Harry Potter"));
        assertEquals(0.36d, confidence, 0.00001d);
    }
}
