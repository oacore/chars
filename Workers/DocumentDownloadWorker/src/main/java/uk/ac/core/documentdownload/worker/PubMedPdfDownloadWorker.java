package uk.ac.core.documentdownload.worker;

import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.database.service.document.DocumentDAO;
import uk.ac.core.filesystem.services.FilesystemDAO;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import uk.ac.core.common.model.legacy.DocumentTdmStatus;
import uk.ac.core.database.service.document.DocumentTdmStatusDAO;
import uk.ac.core.database.service.document.RepositoryMetadataDAO;
import uk.ac.core.documentdownload.entities.dao.DocumentDownloadDAO;

@Service
public class PubMedPdfDownloadWorker {
    private static final Logger log = LoggerFactory.getLogger(PubMedPdfDownloadWorker.class);

    private Integer repositoryId = 150;
    
    @Autowired
    private DocumentDAO mySQLDocumentDAO;
    
    @Autowired
    RepositoryMetadataDAO repositoryMetadataDAO;
    
    @Autowired
    DocumentTdmStatusDAO documentTdmStatusDAO;
    @Autowired
    private FilesystemDAO filesystemDAO;
    @Autowired
    private DocumentDownloadDAO documentDownloadDAO;

    private final AtomicInteger itemsProcessed = new AtomicInteger();
    private final AtomicInteger itemsSkipped = new AtomicInteger();
    private final String documentDirectory = "/data/remote/core/pubmed/ftp/";

    public void processPdfDownload() {
        //We are not to download from this file for licensing reasons. See CORE-2629
        //processFile("oa_file_list.csv");
        processFile("oa_comm_use_file_list.csv");
        processFile("oa_non_comm_use_pdf.csv", true);
    }

    private void processFile(String fileName) {
        processFile(fileName, false);
    }

    private void processFile(String fileName, boolean isTDM) {
        String filePath = checkTaskFile(fileName);
        try (BufferedReader br = new BufferedReader(
                new FileReader(filePath))) {
            String line;
            br.readLine();
            while((line = br.readLine()) != null) {
                itemsProcessed.incrementAndGet();
                String[] lineArr = line.split(",");
                String pmcId = lineArr[2];
                // Does the item in the csv exist in CORE?
                Integer documentId = getDocumentIdByPmcId(pmcId);
                log.info("documentId: '{}', Url: '{}', PMC id: '{}'", documentId, lineArr[0], pmcId);

                if(documentId != null && !new File(filesystemDAO.getPdfPath(documentId, repositoryId)).exists()) {
                    updateOneDocument(lineArr[0], documentId, isTDM);
                } else {
                    log.info("File already downloaded: " + documentId);
                    itemsSkipped.incrementAndGet();
                }
            }

        } catch (FileNotFoundException e) {
            log.error("Ohh, no such file", e);
        } catch (IOException e) {
            log.error("Unexpected issue", e);
        } finally {
            documentDownloadDAO.flushDocumentStatus();
        }
    }

    private String checkTaskFile(String fileName) {
        String folderPath = "/data/remote/core/pubmed/";
        File folder = new File(folderPath);
        if(!folder.exists()) {
            folder.mkdir();
        }
        File tmpDir = new File(this.documentDirectory);
        if(!tmpDir.exists()) {
            tmpDir.mkdir();
        }


        try {
            File file = new File(folderPath + fileName);
            if (file.exists()) {
                LocalDate fileCreationDate =
                        new Date(file.lastModified()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                if (fileCreationDate.isBefore(LocalDate.now().minusDays(7))) {
                    removeFile(file.getPath());
                    downloadFile(fileName, folderPath);
                } else {
                    log.info("File created less than 7 days ago");
                }
            } else {
                downloadFile(fileName, folderPath);
            }

            return folderPath + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Error in updating pubmed task file", e);
        }
    }

    private Integer getDocumentIdByPmcId(String pmc) {
        String oai = "oai:pubmedcentral.nih.gov:" + pmc.replace("PMC", "");
        return repositoryMetadataDAO.getIdDocumentByOai(oai, this.repositoryId);
    }

    private void updateOneDocument(String url, int documentId, boolean isTDM) {
        try {
            String outputPath = null;
            if(url.endsWith(".pdf")) {
                log.info("Pdf {} should be downloaded ", url);
                String downloadPath = downloadFile(url, documentDirectory);
                if (downloadPath != null) {
                    String localOutputPath = filesystemDAO.getPdfPath(documentId, repositoryId);
                    Files.move(Paths.get(downloadPath), Paths.get(localOutputPath), StandardCopyOption.REPLACE_EXISTING); 
                    // If Files.move didn't error, set outputPath = localOutputPath
                    outputPath = localOutputPath;
                }
            } else if(url.endsWith(".tar.gz")) {
                log.info("It is tar.gz");
                String archive = downloadFile(url, documentDirectory);
                if(archive != null) {
                    String unpackFolder = null;
                    try {
                        unpackFolder = unpackTarGz(archive);
                        outputPath = processPdfInsideFolder(unpackFolder, documentId);
                    } finally {
                        if (unpackFolder != null) {
                            removeFolder(unpackFolder);
                        }
                    }
                }
            } else {
                log.error("Unexpected file type: '{}'", url);
            }

            if(isTDM) {
                DocumentTdmStatus documentTdmStatus = new DocumentTdmStatus();
                documentTdmStatus.setIdDocument(documentId);
                documentTdmStatus.setTdmOnly(true);
                documentTdmStatusDAO.insertOrUpdateTdmStatus(documentTdmStatus);
            }

            if (outputPath == null) {
                documentDownloadDAO.setDownloadUnsuccessful(documentId);
            } else { 
                documentDownloadDAO.setDownloadSuccessful(documentId, outputPath, outputPath);
            }            
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String unpackTarGz(String path) throws IOException {
        String destinationPath = documentDirectory;

        File archive = new File(path);
        File destination = new File(destinationPath);

        Archiver archiver = ArchiverFactory.createArchiver(archive);
        archiver.extract(archive, destination);

        return destinationPath + archive.getName().split("\\.")[0];
    }

    /**
     * 
     * @param folderPath
     * @param articleId
     * @return String Path to PDF
     * @throws IOException 
     */
    private String processPdfInsideFolder(String folderPath, int articleId) throws IOException {
        File folder = new File(folderPath);
        for (final File fileEntry : folder.listFiles()) {
            if(fileEntry.getName().endsWith(".pdf")) {
                String documentName = this.filesystemDAO.getPdfPath(articleId, 150);
                String[] documentPath = documentName.split("/");

                if(!new File(folderPath + "/" + documentPath[documentPath.length -1]).exists()) {
                    Files.move(fileEntry.toPath(), Paths.get(documentName), StandardCopyOption.REPLACE_EXISTING); 
                    return documentName;
                } else {
                    log.warn("File '{}' already exist", documentName);
                }
            }
        }
        log.warn("No pdf file in '{}' folder", folderPath);
        return null;
    }

    private void removeFile(String path) throws IOException {
        Files.deleteIfExists(Paths.get(path));
    }

    private void removeFolder(String path) throws IOException {
        Files.walk(Paths.get(path))
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }

    private String downloadFile(String url, String folderPath) throws IOException {
        log.info("Start downloading file from '{}'", url);

        String baseUrl = "https://ftp.ncbi.nlm.nih.gov/pub/pmc/";

        String[] urlPath = url.split("/");
        String documentName = folderPath + urlPath[urlPath.length - 1];
        if(!new File(folderPath + "/" + urlPath[urlPath.length - 1]).exists()) {
            URL documentUri = new URL(baseUrl + url);
            ReadableByteChannel rbc = Channels.newChannel(documentUri.openStream());
            FileOutputStream fos = new FileOutputStream(documentName);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            log.info("End downloading file");
            return documentName;
        } else {
            log.info("File '{}' already exist", urlPath[urlPath.length - 1]);
        }
        return documentName;

    }
    
    public int getItemsProcessed() {
        return this.itemsProcessed.get();
    }
    
    public int getSkippedItems() {
        return this.itemsSkipped.get();
    }
}
