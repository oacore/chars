package uk.ac.core.extractmetadata.periodic.crossref.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import uk.ac.core.common.exceptions.CHARSException;
import uk.ac.core.common.model.task.TaskItemStatus;
import uk.ac.core.database.service.document.RawMetadataDAO;
import uk.ac.core.extractmetadata.periodic.crossref.runnables.DeleteAmbiguousRecordsTask;
import uk.ac.core.extractmetadata.periodic.crossref.runnables.ReportMalformedRecordsTask;
import uk.ac.core.extractmetadata.periodic.crossref.runnables.WriteRecordsTask;
import uk.ac.core.extractmetadata.periodic.crossref.service.CrossrefReharvestService;
import uk.ac.core.extractmetadata.periodic.crossref.util.CrossrefMetadataCallbackHandler;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.ArticleMetadataPersist;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.ArticleMetadataPersistFactory;
import uk.ac.core.extractmetadata.worker.oaipmh.GetRecords.CrossrefSaxHandler;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.supervisor.client.SupervisorClient;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

@Service
public class CrossrefReharvestServiceImpl implements CrossrefReharvestService {
    private static final Logger log = LoggerFactory.getLogger(CrossrefReharvestServiceImpl.class);
    private static final String SQL_QUERY = "" +
            "select " +
            "   drm.id as `id`, " +
            "   d.id_document as `docId`, " +
            "   drm.metadata as `rawMetadata`, " +
            "   d.oai as `oai`, " +
            "   drm.datetime as `datetime` " +
            "from document d " +
            "    left join document_metadata dm on d.id_document = dm.id_document " +
            "    left join document_raw_metadata drm on d.id_document = drm.id_document " +
            "    left join mucc_document_metadata mdm on d.id_document = mdm.coreId " +
            "where d.id_repository = 4786 and " +
            "      dm.doi is null and " +
            "      d.deleted = 0 and " +
            "      mdm.doi is null";
    private static final int CROSSREF_REPO_ID = 4786;
    @Value("${cr.reharvest.querysize:100000}")
    private int QUERY_SIZE;
    private int BATCH_SIZE = 1_000;

    private final JdbcTemplate jdbcTemplate;
    private final FilesystemDAO filesystemDAO;
    private final ArticleMetadataPersistFactory persistFactory;
    private final SupervisorClient supervisorClient;
    private final RawMetadataDAO rawMetadataDAO;
    private ArticleMetadataPersist persist;
    private final List<Integer> idsToReindex;
    private final ThreadPoolExecutor executor;


    @Autowired
    public CrossrefReharvestServiceImpl(
            JdbcTemplate jdbcTemplate,
            FilesystemDAO filesystemDAO,
            ArticleMetadataPersistFactory persistFactory,
            SupervisorClient supervisorClient,
            RawMetadataDAO rawMetadataDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.filesystemDAO = filesystemDAO;
        this.persistFactory = persistFactory;
        this.supervisorClient = supervisorClient;
        this.rawMetadataDAO = rawMetadataDAO;
        this.idsToReindex = new ArrayList<>();
        this.executor = new ThreadPoolExecutor(
                3,
                5,
                2, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(100),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    @Override
    public void initPersist(TaskItemStatus taskItemStatus) {
        this.persist = this.persistFactory.create(taskItemStatus, CROSSREF_REPO_ID);
    }

    @Override
    public File getRawMetadataFile() {
        File tmpFile = this.filesystemDAO.createCrossrefMetadataFile();
        if (tmpFile == null) {
            log.warn("Exception raised during creation of temporary file");
            log.warn("Please check the logs above");
            return null;
        }
        try {
            this.retrieveAndWriteMetadataOnTheFly(tmpFile);
        } catch (RuntimeException | IOException | ExecutionException | InterruptedException e) {
            log.error("Exception raised while writing metadata to the temporary file", e);
            return null;
        }
        return tmpFile;
    }

    @Override
    public void parseMetadata(File tmpFile) throws SAXException {
        if (tmpFile == null) {
            log.info("The file obj is null");
            log.info("Cannot continue to parse");
            return;
        }
        try {
            SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
            CrossrefSaxHandler handler = new CrossrefSaxHandler(this.persist);

            log.info("Parser and handler configured");
            long start = System.currentTimeMillis(), end;

            saxParser.parse(tmpFile, handler);

            end = System.currentTimeMillis();
            log.info("Parsing done in {} ms", end - start);
        } catch (ParserConfigurationException e) {
            log.error("Exception raised while configuring parser", e);
        } catch (IOException e) {
            log.error("I/O Exception raised while parsing the metadata", e);
        }
    }

    @Override
    public boolean deleteTmpFile(File tmpFile) {
        log.info("Deleting temporary file: {}", tmpFile.getPath());
        return this.filesystemDAO.deleteFile(tmpFile.getPath());
    }

    @Override
    public void flushRecordsToDatabase() {
        log.info("Flushing records to the database ...");
        // we do not generate statistics for Crossref
        this.persist.finalise(false);
        log.info("Done");
    }

    @Override
    public void scheduleReindex() {
        log.info("Start scheduling re-index for documents ...");
        long start = System.currentTimeMillis();
        final int delay = 100; // ms
        try {
            for (Integer docId : this.idsToReindex) {
                this.supervisorClient.sendIndexItemRequest(docId);
                Thread.sleep(delay);
                // DO NOT DO `works` INDEXING
                // BECAUSE IT WON'T BE UPDATED ON `articles` INDEX FOR QUITE A LONG TIME
                // IT WOULD ONLY LOAD THE QUEUE
            }
            long end = System.currentTimeMillis();
            log.info("Done in {} ms", end - start);
            this.idsToReindex.clear();
        } catch (CHARSException ignored) {
            // ignored because it should never be raised
            // `supervisorClient` initialised with bean of better HttpSupervisorClient
            // which does not throw an exception
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int processedCount() {
        return this.idsToReindex.size();
    }

    private void retrieveAndWriteMetadataOnTheFly(final File tmpFile) throws IOException, ExecutionException, InterruptedException {
        log.info("Start retrieving database records ...");
        log.info("Query size is set to {}", QUERY_SIZE);
        long start = System.currentTimeMillis();
        String limit = " limit ?, ?"; // limit offset, batch_size

        // TODO: HOW DO WE AVOID PROCESSING THE SAME IDS EVERY TIME
//        File latestMalformedReport = this.filesystemDAO.getLatestCrossrefRecordsMalformedReport();
//        List<Integer> malformedRecordsIds = this.getIdsFromMalformedReport(latestMalformedReport);
        CrossrefMetadataCallbackHandler handler = new CrossrefMetadataCallbackHandler(new ArrayList<>());

        int offset = 0;

        // for debugging
        // may delete later
        if (QUERY_SIZE <= 1_000) {
            BATCH_SIZE = QUERY_SIZE;
        }

        while (offset + BATCH_SIZE <= QUERY_SIZE) {
            String sql = SQL_QUERY + limit;
            Object[] args = {offset, BATCH_SIZE};
            log.info("Executing SQL: {}", sql);
            log.info("Offset = {}", offset);
            log.info("Limit = {}", BATCH_SIZE);
            this.jdbcTemplate.query(sql, args, handler);
            offset += BATCH_SIZE;
        }

        File malformedReportFile = this.filesystemDAO.createEmptyMalformedReportFile();

        List<Runnable> tasks = Arrays.asList(
                // DELETE AMBIGUOUS RECORDS
                new DeleteAmbiguousRecordsTask(handler.getToBeDeleted(), this.jdbcTemplate),
                // REPORT RECORDS WITH MALFORMED METADATA
                new ReportMalformedRecordsTask(malformedReportFile, handler.getToBeReported()),
                // DEDUPLICATE LIST AND WRITE TO THE FILE AND DELETE DUPLICATES FROM DB
                new WriteRecordsTask(
                        tmpFile, handler.getToBeWritten(), this.rawMetadataDAO, this.jdbcTemplate, this.idsToReindex)
        );

        List<Future<?>> futures = new ArrayList<>();
        for (Runnable task : tasks) {
            futures.add(this.executor.submit(task));
        }

        // START THESE TASKS IN A THREAD POOL EXECUTOR
        // SHUTDOWN EXECUTOR AND WAIT FOR TERMINATION
        // (kinda dump, but it works)
        for (Future<?> f : futures) {
            f.get();
        }

        long end = System.currentTimeMillis();
        log.info("Number of records successfully written: {}", handler.getRecordsWritten());
        log.info("Number of records with malformed metadata: {}", handler.getRecordsMalformed());
        log.info("Number of records with null metadata: {}", handler.getRecordsNoMetadata());
        log.info("Done in {} ms", end - start);
    }

    private List<Integer> getIdsFromMalformedReport(File latestMalformedReport) {
        if (latestMalformedReport == null) {
            return new ArrayList<>();
        }
        try {
            Scanner scanner = new Scanner(latestMalformedReport);
            List<Integer> results = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split(",");
                String docId = line[0];
                if (!"drm_id".equals(docId)) {
                    results.add(Integer.parseInt(docId));
                }
            }

            scanner.close();
            return results;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public ArticleMetadataPersist getPersist() {
        return persist;
    }
}
