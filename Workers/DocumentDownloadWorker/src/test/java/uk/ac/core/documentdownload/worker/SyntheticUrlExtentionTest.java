package uk.ac.core.documentdownload.worker;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 * @author samuel
 */
public class SyntheticUrlExtentionTest {

    public SyntheticUrlExtentionTest() {
    }

    /**
     * Test of hasExtension method, of class SyntheticUrlExtension.
     */
    @Test
    public void testUrlHasQuestAndHasExtention() {
        SyntheticUrlExtension e = new SyntheticUrlExtension("https://ore.exeter.ac.uk/repository/bitstream/handle/10871/14764/MeadhamH_LitReview.pdf?sequence=1&isAllowed=y");
        assertTrue(e.hasExtension());
    }

    @Test
    public void testUrlHasNoQuestAndHasExtention() {
        SyntheticUrlExtension e = new SyntheticUrlExtension("https://ore.exeter.ac.uk/repository/bitstream/handle/10871/14764/MeadhamH_LitReview.pdf");
        assertTrue(e.hasExtension());
    }

    /**
     * Test of getExtension method, of class SyntheticUrlExtension.
     */
    @Test
    public void testPdfGetExtention() {
        SyntheticUrlExtension e = new SyntheticUrlExtension("https://ore.exeter.ac.uk/repository/bitstream/handle/10871/14764/MeadhamH_LitReview.pdf?sequence=1&isAllowed=y");
        assertTrue(e.hasExtension());
    }

    @Test
    public void testNoExtention() {
        SyntheticUrlExtension e = new SyntheticUrlExtension("https://ore.exeter.ac.uk/repository/bitstream/handle/10871/14764/MeadhamH_LitReview");
        assertFalse(e.hasExtension());
    }

    @Test
    public void testRawdomainExtention() {
        SyntheticUrlExtension e = new SyntheticUrlExtension("https://ore.exeter.ac.uk");
        assertFalse(e.hasExtension());
    }

    @Test
    public void testRawdomainWithSlashExtention() {
        SyntheticUrlExtension e = new SyntheticUrlExtension("https://ore.exeter.ac.uk/");
        assertFalse(e.hasExtension());
    }
}
