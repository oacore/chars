package uk.ac.core.services.web.ref.database;


import uk.ac.core.services.web.ref.model.FullTextReportDTO;
import uk.ac.core.services.web.ref.model.RefReportDTO;

import java.util.List;


public interface RefReportDAO {

    List<RefReportDTO> getReportData(String doi);

    List<RefReportDTO> getMuccReportData(String doi);

    List<FullTextReportDTO> getFullTextReportData(String doi);

}
