package uk.ac.core.extractmetadata.periodic.crossref.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import uk.ac.core.extractmetadata.periodic.crossref.model.CrossrefMetadata;

import javax.xml.parsers.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class CrossrefMetadataCallbackHandler implements RowCallbackHandler {
    private static final Logger log = LoggerFactory.getLogger(CrossrefMetadataCallbackHandler.class);
    private final List<CrossrefMetadata> toBeWritten;
    private final List<Integer> toBeDeleted;
    private final List<CrossrefMetadata> toBeReported;
    private final List<Integer> toSkip;

    public CrossrefMetadataCallbackHandler(List<Integer> idsToSkip) {
        this.toBeWritten = new ArrayList<>();
        this.toBeDeleted = new ArrayList<>();
        this.toBeReported = new ArrayList<>();
        this.toSkip = idsToSkip;
    }

    private int recordsWritten;
    private int recordsMalformed;
    private int recordsNoMetadata;

    @Override
    public void processRow(ResultSet rs) throws SQLException {
        CrossrefMetadata cm = new CrossrefMetadata();

        int drmId = rs.getInt("id");
        if (this.toSkip.contains(drmId)) {
            return;
        }
        int docId = rs.getInt("docId");
        String oai = rs.getString("oai");
        Timestamp datetime = rs.getTimestamp("datetime");
        String metadata = rs.getString("rawMetadata");

        cm.setId(drmId);
        cm.setDocId(docId);
        cm.setOai(oai);
        cm.setDatetime(datetime);

        try {
            if (metadata != null) {
                boolean wellFormed = this.isXmlWellFormed(metadata);
                boolean oaiMatch = this.doesOaiMatches(metadata, cm.getOai());

                if (wellFormed && oaiMatch) {
                    this.toBeWritten.add(cm);
                    this.recordsWritten++;
                }
                if (!wellFormed) {
                    this.toBeReported.add(cm);
                    this.recordsMalformed++;
                }
                if (!oaiMatch) {
                    this.toBeDeleted.add(cm.getId());
                }
            } else {
                this.recordsNoMetadata++;
                log.info("Metadata for document (ID: {}) is null", cm.getDocId());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean doesOaiMatches(String metadata, String oai) {
        return metadata.contains(oai.trim());
    }

    private boolean isXmlWellFormed(String metadata) throws IOException {
        boolean wellFormed = true;
        try {
            InputStream is = new ByteArrayInputStream(metadata.getBytes(StandardCharsets.UTF_8));

            SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
            parser.parse(is, new DefaultHandler());
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            wellFormed = false;
        }
        return wellFormed;
    }

    public List<CrossrefMetadata> getToBeReported() {
        return toBeReported;
    }

    public int getRecordsMalformed() {
        return recordsMalformed;
    }

    public int getRecordsWritten() {
        return recordsWritten;
    }

    public int getRecordsNoMetadata() {
        return recordsNoMetadata;
    }

    public List<Integer> getToBeDeleted() {
        return toBeDeleted;
    }

    public List<CrossrefMetadata> getToBeWritten() {
        return toBeWritten;
    }
}
