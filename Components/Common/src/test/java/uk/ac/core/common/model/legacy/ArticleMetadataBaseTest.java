/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.common.model.legacy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author samuel
 */
public class ArticleMetadataBaseTest {
    
    public ArticleMetadataBaseTest() {
    }

    /**
     * Test of formatName method, of class ArticleMetadataBase.
     */
    @Test
    public void testFormatName() {
        System.out.println("formatName");
        String author = "Eric, Vincent Bindah";
        ArticleMetadataBase instance = new ArticleMetadataBase();
        String expResult = "Vincent Bindah Eric";
        String result = instance.formatName(author);
        assertEquals(expResult, result);
    }

}
