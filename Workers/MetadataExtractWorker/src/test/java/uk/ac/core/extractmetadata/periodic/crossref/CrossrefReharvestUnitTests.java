package uk.ac.core.extractmetadata.periodic.crossref;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import uk.ac.core.extractmetadata.periodic.crossref.model.CrossrefMetadata;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CrossrefReharvestUnitTests {
    @Test
    public void testCrossrefMetadataSorting() throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<CrossrefMetadata> list = new ArrayList<>();

        CrossrefMetadata cm1 = new CrossrefMetadata();
        cm1.setId(698536013);
        cm1.setOai("info:doi/10.1016%2Fj.iatssr.2013.04.001");
        cm1.setDocId(185201920);
        cm1.setDatetime(sdf.parse("2020-11-17 05:03:01"));

        CrossrefMetadata cm2 = new CrossrefMetadata();
        cm2.setId(965031754);
        cm2.setOai("info:doi/10.1016%2Fj.iatssr.2013.04.001");
        cm2.setDocId(185201920);
        cm2.setDatetime(sdf.parse("2021-06-18 20:36:43"));

        list.add(cm1);
        list.add(cm2);

        list.sort((o1, o2) -> {
            if (o1.getDatetime() == null || o2.getDatetime() == null) {
                return 0;
            }
            return o1.getDatetime().compareTo(o2.getDatetime());
        });

        Assertions.assertEquals(cm1.getDatetime(), list.get(0).getDatetime());
    }
}
