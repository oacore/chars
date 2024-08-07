package uk.ac.core.extractmetadata.periodic.crossref.runnables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import uk.ac.core.common.model.legacy.DocumentRawMetadata;
import uk.ac.core.database.service.document.RawMetadataDAO;
import uk.ac.core.extractmetadata.periodic.crossref.model.CrossrefMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WriteRecordsTask implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(WriteRecordsTask.class);

    private static final String XML_HEADER = "" +
            "<?xml version=\"1.1\" encoding=\"UTF-8\"?>" +
            "<harvest>" +
            "<OAI-PMH xmlns=\"http://www.openarchives.org/OAI/2.0/\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/ " +
            "http://www.openarchives.org/OAI/2.0/OAI-PMH.xsd\">";
    private static final String XML_FOOTER = "" +
            "</OAI-PMH>" +
            "</harvest>";
    private static final String DELETE_QUERY = "" +
            "delete from document_raw_metadata where id = ?";

    private final File tmpFile;
    private final List<CrossrefMetadata> duplicatedList;
    private final RawMetadataDAO rawMetadataDAO;
    private final JdbcTemplate jdbcTemplate;
    private final List<Integer> idsToReindex;

    public WriteRecordsTask(
            File tmpFile,
            List<CrossrefMetadata> duplicatedList,
            RawMetadataDAO rawMetadataDAO,
            JdbcTemplate jdbcTemplate,
            List<Integer> idsToReindex) {
        this.tmpFile = tmpFile;
        this.duplicatedList = duplicatedList;
        this.rawMetadataDAO = rawMetadataDAO;
        this.jdbcTemplate = jdbcTemplate;
        this.idsToReindex = idsToReindex;
    }

    @Override
    public void run() {
        try {
            log.info("Start writing metadata into a file ...");
            Files.write(
                    tmpFile.toPath(),
                    XML_HEADER.getBytes(),
                    StandardOpenOption.WRITE
            );

            final List<CrossrefMetadata> duplicates = new ArrayList<>();
            log.info("Deduplicating list of Crossref metadata ...");
            List<CrossrefMetadata> toBeWritten = this.deduplicateMetadataList(duplicatedList, duplicates);
            log.info("Done, found {} records to be removed because they are duplicates", duplicates.size());
            for (CrossrefMetadata cm : toBeWritten) {
                DocumentRawMetadata drm = this.rawMetadataDAO.getDocumentRawMetadataByDrmId(cm.getId());
                if (drm != null) {
                    Files.write(
                            tmpFile.toPath(),
                            this.stripNonValidXMLCharacters(drm.getMetadata()).getBytes(),
                            StandardOpenOption.APPEND);
                    this.idsToReindex.add(cm.getDocId());
                }
            }

            Files.write(
                    tmpFile.toPath(),
                    XML_FOOTER.getBytes(),
                    StandardOpenOption.APPEND
            );

            log.info("Deleting duplicate records if there any ...");
            for (CrossrefMetadata cm : duplicates) {
                int drmId = cm.getId();
                this.jdbcTemplate.update(DELETE_QUERY, drmId);
                log.info("Deleted `document_raw_metadata` record with ID {}", drmId);
                int delay = 1000; // ms
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            log.info("Done");
            log.info("Finished deduplicating and writing metadata into the file");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<CrossrefMetadata> deduplicateMetadataList(
            List<CrossrefMetadata> list,
            final List<CrossrefMetadata> toBeRemoved) {
        Map<Integer, List<CrossrefMetadata>> idToMetadata = new HashMap<>();
        for (CrossrefMetadata cm : list) {
            int docId = cm.getDocId();
            if (idToMetadata.get(docId) == null) {
                idToMetadata.put(docId, new ArrayList<>());
                idToMetadata.get(docId).add(cm);
            } else {
                idToMetadata.get(docId).add(cm);
            }
        }
        return idToMetadata.values().stream()
                .map(duplicates -> {
                    duplicates.sort((o1, o2) -> {
                        if (o1.getDatetime() == null || o2.getDatetime() == null) {
                            return 0;
                        }
                        return o1.getDatetime().compareTo(o2.getDatetime());
                    });
                    toBeRemoved.addAll(duplicates.subList(1, duplicates.size()));
                    return duplicates.get(0);
                }).collect(Collectors.toList());
    }

    // THIS METHOD IS A COPY-PASTE OF METHOD
    //      `ORG.oclc.oai.harvester2.verb.HarvesterVerb.stripNonValidXMLCharacters(String)`
    //  IT WAS EASIER TO COPY-PASTE IT INSTEAD OF IMPORTING DEPENDENCIES
    private String stripNonValidXMLCharacters(String in) {
        StringBuffer out = new StringBuffer(); // Used to hold the output.
        char current; // Used to reference the current character.

        if (in == null || ("".equals(in))) return ""; // vacancy test.
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i); // NOTE: No IndexOutOfBoundsException caught here; it should not happen.
            if (
                    ((current == 0x9) ||
                            (current == 0xA) ||
                            (current == 0xD) ||
                            (((current >= 0x20) && (current <= 0x7E))) ||
                            (((current >= 0xA0) && (current <= 0xD7FF))) ||
                            ((current >= 0xF900) && (current <= 0xFFFD)) ||
                            ((current >= 0x10000) && (current <= 0x10FFFF))))
                out.append(current);
        }
        return out.toString();
    }
}
