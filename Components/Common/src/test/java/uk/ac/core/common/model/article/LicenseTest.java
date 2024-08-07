package uk.ac.core.common.model.article;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LicenseTest {

    /**
     *
     */
    @Test
    public void testOpenAccess() {
        assertFalse(new License("").isOpenAccess());
        assertFalse(new License(null).isOpenAccess());
        assertTrue(new License("https://creativecommons.org/licenses/by/4.0/").isOpenAccess());
        assertTrue(new License("CC-BY-NC").isOpenAccess());
        assertTrue(new License("CC-BY").isOpenAccess());
    }

}