package uk.ac.core.services.web.affiliations.service;

import org.grobid.core.data.BiblioItem;
import org.grobid.core.engines.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.services.web.affiliations.exception.GrobidExtractionException;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryRequest;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryResponse;
import uk.ac.core.services.web.affiliations.model.AffiliationsDiscoveryResponseItem;
import uk.ac.core.services.web.affiliations.model.grobid.GrobidAuthor;
import uk.ac.core.services.web.affiliations.model.grobid.GrobidReport;
import uk.ac.core.services.web.affiliations.util.GrobidAuthorMapper;

import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GrobidExtractionService {
    private static final Logger logger = LoggerFactory.getLogger(GrobidExtractionService.class);
    private static final String GROBID_PATH = "/data/remote/core/filesystem/grobid/";
    private static final String PDF_PATH = "/data/remote/core/filesystem/pdf/";
    private static final String PDF_PATH_OLD = "/data/filesystem/core/filesystem/pdf/";

    private final GrobidInterpreterService interpreter;
    private final Engine engine;

    @Autowired
    public GrobidExtractionService(GrobidInterpreterService interpreter, Engine engine) {
        this.interpreter = interpreter;
        this.engine = engine;
    }

    @PreDestroy
    public void preDestroy() {
        logger.info("Releasing resources ...");
        try {
            logger.info("Closing GROBID engine ...");
            this.engine.close();
        } catch (Exception e) {
            logger.error("Exception while releasing GROBID engine resource", e);
        }
    }

    public AffiliationsDiscoveryResponse extract(AffiliationsDiscoveryRequest request) {
        logger.info("Extracting affiliations with GROBID engine ...");
        AffiliationsDiscoveryResponse response = new AffiliationsDiscoveryResponse();
        response.setCoreId(request.getCoreId());
        response.setRepoId(request.getRepoId());
        response.setDateCreated(new Date());
        response.setSource("grobid");
        long start = System.currentTimeMillis(), end;
        try {
            /**
             * STEP 1
             * CHECK IF TEI-XML FILE EXISTS
             * IF NOT - GENERATE ONE
             */

            File teiXmlFile = new File(
                    GROBID_PATH + request.getRepoId() + "/" + request.getCoreId() + ".tei.xml");
            if (!teiXmlFile.exists()) {
                logger.info("File {} does not exist", teiXmlFile.getPath());
                logger.info("Generating ...");
                this.generateTeiXml(request);
            } else {
                logger.info("File {} does exist", teiXmlFile.getPath());
                logger.info("Skip generation ...");
            }

            /**
             * STEP 2
             * PARSE TEI-XML FILE
             */

            List<AffiliationsDiscoveryResponseItem> hits =
                    this.parseTeiXml(teiXmlFile);

            /**
             * STEP 3
             * RETURN RESPONSE
             */

            response.setHits(hits);
            response.setCount(hits.size());
            response.setMessage("OK");
            end = System.currentTimeMillis();
            response.setTook(end - start);
        } catch (Exception e) {
            logger.error("Exception occurred", e);
            response.setCount(0);
            response.setMessage("Error message: " + e.getMessage());
            end = System.currentTimeMillis();
            response.setTook(end - start);
        }
        logger.info("GROBID engine method finished in {} ms", response.getTook());
        logger.info("Extracted {} affiliations", response.getCount());
        return response;
    }

    private List<AffiliationsDiscoveryResponseItem> parseTeiXml(File teiXmlFile) throws GrobidExtractionException {
        List<GrobidAuthor> authors = this.interpreter.parseTei(teiXmlFile);
        return authors.stream()
                .flatMap(new GrobidAuthorMapper())
                .collect(Collectors.toList());
    }

    private void generateTeiXml(AffiliationsDiscoveryRequest request) throws GrobidExtractionException, IOException {
        logger.info("Preparing GROBID engine object ...");
        try {
            File fullText = this.getPdfFromDisk(request.getRepoId(), request.getCoreId());
            if (fullText == null) {
                logger.error("Full text does not exist: repoId = {}, coreId = {}",
                        request.getRepoId(), request.getCoreId());
                throw new GrobidExtractionException(String.format(
                        "Full text does not exist: repoId = %d, coreId = %d",
                        request.getRepoId(), request.getCoreId()
                ));
            } else {
                logger.info("Full text does exist");
            }
            logger.info("Engine object ready, start processing ...");
            BiblioItem biblioItem = new BiblioItem();
            String tei = engine.processHeader(
                    fullText.getPath(),
                    1,
                    biblioItem);
            logger.info("Processing finished, creating file ...");
            File teiXmlFile = new File(
                    GROBID_PATH + request.getRepoId() + "/" + request.getCoreId() + ".tei.xml");
            logger.info("File {} created: {}", teiXmlFile.getPath(), teiXmlFile.createNewFile());
            logger.info("Ready to write into the file");
            Files.write(teiXmlFile.toPath(), tei.getBytes());
            logger.info("Successfully written TEI into file");
            logger.info("Finish generating TEI-XML");
        } catch (Exception e) {
            logger.error("Exception occurred", e);
            throw e;
        }
    }

    private File getPdfFromDisk(Integer repoId, Integer coreId) {
        File pdf = new File(PDF_PATH + repoId + "/" + coreId + ".pdf");
        File pdfOld = new File(PDF_PATH_OLD + repoId + "/" + coreId + ".pdf");
        if (pdf.exists()) {
            return pdf;
        } else if (pdfOld.exists()) {
            return pdfOld;
        } else {
            return null;
        }
    }

    public GrobidReport processRepository(Integer repoId, boolean overwrite) {
        logger.info("Repository processing requested ...");
        logger.info("Repository ID = {}, overwrite = {}", repoId, overwrite);
        logger.info("Preparing GROBID engine object ...");
        long start = System.currentTimeMillis(), end;
        int totalCounter = 0, successCounter = 0, skippedCounter = 0;
        try {
            logger.info("GROBID engine object ready");
            logger.info("Checking validity of the repository in the filesystem ...");
            File repositoryFolder = new File(PDF_PATH + repoId);
            if (!this.checkValidity(repositoryFolder)) {
                throw new GrobidExtractionException(
                        "Repository folder " + repositoryFolder.getPath() + " does not exist, " +
                                "is not a directory or is empty");
            } else {
                logger.info("Repository folder is valid, ready to continue ...");
            }
            File[] pdfs = repositoryFolder
                    .listFiles(pathname -> pathname.getName().endsWith(".pdf"));
            for (File pdf: pdfs) {
                totalCounter++;
                String coreId = pdf.getName().substring(0, pdf.getName().indexOf('.'));
                File teiFile = new File(GROBID_PATH + repoId + "/" + coreId + ".tei.xml");
                if (!teiFile.exists()) {
                    logger.info("File {} does not exist", teiFile.getPath());
                    logger.info("Create the file: {}", teiFile.createNewFile());
                    logger.info("Processing file {} ...", pdf.getPath());
                    BiblioItem biblioItem = new BiblioItem();
                    String tei = engine.processHeader(
                            pdf.getPath(),
                            1,
                            biblioItem
                    );
                    logger.info("Processing file {} finished", pdf.getPath());
                    logger.info("Preparing to save TEI ...");
                    Files.write(teiFile.toPath(), tei.getBytes());
                    logger.info("Successfully written in file {}", teiFile.getPath());
                    successCounter++;
                } else {
                    logger.info("File {} already exist", teiFile.getPath());
                    if (overwrite) {
                        logger.info("Overwriting existing file ...");
                        BiblioItem biblioItem = new BiblioItem();
                        String tei = engine.processHeader(
                                pdf.getPath(),
                                1,
                                biblioItem
                        );
                        logger.info("TEI ready, rewriting the file ...");
                        Files.write(teiFile.toPath(), tei.getBytes());
                        logger.info("Successfully overwritten existing file");
                        successCounter++;
                    } else {
                        logger.info("File {} already exist, skipping ...", teiFile.getPath());
                        skippedCounter++;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred", e);
        }
        logger.info("All files done, preparing report ...");
        GrobidReport report = new GrobidReport();
        report.setRepoId(repoId);
        report.setItemsTotal(totalCounter);
        report.setItemsSuccess(successCounter);
        report.setItemsSkipped(skippedCounter);
        end = System.currentTimeMillis();
        report.setDuration(end - start);
        return report;
    }

    private boolean checkValidity(File folder) {
        boolean exists = folder.exists();
        logger.info("Folder {} exists: {}", folder.getPath(), exists);
        boolean isDir = folder.isDirectory();
        logger.info("Folder {} is directory: {}", folder.getPath(), isDir);
        boolean notEmpty = Objects.requireNonNull(
                folder.listFiles(
                        pathname -> pathname.getName().endsWith(".pdf"))).length != 0;
        logger.info("Folder {} not empty: {}", folder.getPath(), notEmpty);
        return exists && isDir && notEmpty;
    }
}
