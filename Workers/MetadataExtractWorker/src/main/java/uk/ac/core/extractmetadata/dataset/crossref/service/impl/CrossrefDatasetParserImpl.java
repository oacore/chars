package uk.ac.core.extractmetadata.dataset.crossref.service.impl;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.crossref.json.CrossRefDocument;
import uk.ac.core.extractmetadata.dataset.crossref.exception.CrossrefDatasetLockException;
import uk.ac.core.extractmetadata.dataset.crossref.service.CrossrefDatasetParser;
import uk.ac.core.extractmetadata.dataset.crossref.service.CrossrefDatasetReader;
import uk.ac.core.extractmetadata.dataset.crossref.service.CrossrefWorkService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipException;

@Service
public class CrossrefDatasetParserImpl implements CrossrefDatasetParser {
    private static final Logger log = LoggerFactory.getLogger(CrossrefWorkServiceImpl.class);

    private final CrossrefDatasetReader reader;
    private final CrossrefWorkService service;
    private final ObjectMapper jsonMapper;

    @Autowired
    public CrossrefDatasetParserImpl(CrossrefDatasetReader reader, CrossrefWorkService service) {
        this.reader = reader;
        this.service = service;
        this.jsonMapper = new ObjectMapper(new JsonFactory());
    }

    @Override
    public void processBatch() throws CrossrefDatasetLockException {
        File lock = null;
        File jsonFile = null;
        long start, end;

        try {
            final String entryName = this.reader.getNextEntryName();

            if (entryName == null) {
                log.info("ALL BATCHES PROCESSED");
                return;
            } else {
                lock = this.reader.setLock(entryName);
                log.info("Processing file {}", entryName);
            }

            if (lock == null) {
                throw new RuntimeException("Failed to set lock on entry");
            }

            start = System.currentTimeMillis();
            try {
                jsonFile = this.reader.extractEntry(entryName);
            } catch (ZipException ze) {
                return;
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
            end = System.currentTimeMillis();
            log.info("JSON file extracted in {} ms", end - start);

            byte[] bytes = Files.readAllBytes(jsonFile.toPath());
            String rawJson = new String(bytes);

            List<CrossRefDocument> crossrefWorks = this.mapToCrossrefDocumentList(rawJson);

            int counter = 0, total = crossrefWorks.size();
            log.info("Transformed raw JSON to models");
            log.info("There are {} records to process ...", total);

            for (CrossRefDocument work : crossrefWorks) {
                counter++;
                if (counter % 500 == 0) {
                    log.info("Processed {} out of {}", counter, total);
                }
                Optional<Integer> coreId = this.service.matchCrossrefWork(work);
                if (coreId.isPresent()) {
                    log.info("Found Crossref document with DOI {} in CORE: ID {}", work.getDOI(), coreId.get());
                    this.service.updateExistingCoreRecord(coreId.get(), work);
                } else {
                    log.info("Going to add new record to CORE with DOI: {}", work.getDOI());
                    this.service.addNewCoreRecord(work);
                }
            }

            this.reader.checkpoint(entryName);
            log.info("Entry {} saved as a checkpoint", entryName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (jsonFile != null) {
                try {
                    Files.delete(jsonFile.toPath());
                    log.info("File {} deleted", jsonFile.getName());
                } catch (IOException e) {
                    log.error("Failed to delete file {}", jsonFile.getPath());
                    log.error("", e);
                }
            }
            if (lock != null) {
                this.reader.releaseLock(lock);
            }
        }
    }

    @Override
    public boolean hasNextBatch() throws CrossrefDatasetLockException {
        return this.reader.getNextEntryName() != null;
    }

    @Override
    public CrossrefDatasetReader getReader() {
        return this.reader;
    }

    private List<CrossRefDocument> mapToCrossrefDocumentList(String rawJson) throws JsonProcessingException {
        long start, end;
        start = System.currentTimeMillis();
        log.info("Start mapping raw JSON strings to Java models ...");
        final List<CrossRefDocument> works = new ArrayList<>();
        JsonNode root = this.jsonMapper.readTree(rawJson);
        JsonNode items = root.get("items");
        if (items.isArray()) {
            items.forEach(item -> {
                String itemString = item.toPrettyString();
                try {
                    works.add(CrossRefDocument.fromString(itemString));
                } catch (IOException ignored) {
                }
            });
        }
        end = System.currentTimeMillis();
        log.info("Done in {} ms", end - start);
        return works;
    }
}
