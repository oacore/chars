package uk.ac.core.filesystem.services.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.common.model.article.DeletedStatus;
import uk.ac.core.filesystem.confiuration.FilesystemConfiguration;
import uk.ac.core.filesystem.services.FilesystemDAO;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

/**
 * @author lucasanastasiou
 */
@Service
public class HardDiskFilesystemDAO implements FilesystemDAO {

    final static private String FILENAME_DATE_FORMAT = "yyyy-MM-dd";
    final static private SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(FILENAME_DATE_FORMAT);

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(HardDiskFilesystemDAO.class);

    String BASE_STORAGE_PATH;
    String DOCUMENT_STORAGE_PATH;
    String TEXT_STORAGE_PATH;
    String GROBID_STORAGE_PATH;
    String METADATA_STORAGE_PATH;
    String GROBID_EXTRACTED_IMAGES_PATH;
    String PREVIEW_STORAGE_PATH;
    String METADATA_PAGE_DOWNLOAD_DIR;

    @Autowired
    FilesystemConfiguration filesystemConfiguration;

    @PostConstruct
    private void init() {
        BASE_STORAGE_PATH = filesystemConfiguration.BASE_STORAGE_PATH;
        DOCUMENT_STORAGE_PATH = filesystemConfiguration.DOCUMENT_STORAGE_PATH;
        TEXT_STORAGE_PATH = filesystemConfiguration.TEXT_STORAGE_PATH;
        GROBID_STORAGE_PATH = filesystemConfiguration.GROBID_STORAGE_PATH;
        PREVIEW_STORAGE_PATH = filesystemConfiguration.PREVIEW_STORAGE_PATH;
        METADATA_STORAGE_PATH = filesystemConfiguration.METADATA_STORAGE_PATH;
        GROBID_EXTRACTED_IMAGES_PATH = filesystemConfiguration.GROBID_EXTRACTED_IMAGES_PATH;
        METADATA_PAGE_DOWNLOAD_DIR = filesystemConfiguration.METADATA_PAGE_DOWNLOAD_DIR;
    }

    private String getStoragePath(String storagePath) {
        String dataStoragePath = null;

        if ((!storagePath.isEmpty()) && (storagePath.startsWith("/"))) {
            dataStoragePath = storagePath;
        } else {
            dataStoragePath = BASE_STORAGE_PATH + storagePath;
        }
        if (!dataStoragePath.endsWith("/")) {
            dataStoragePath += "/";
        }
        return dataStoragePath;
    }

    public String getMetadataStoragePath(String metadataDirectoryName) {
        return getStoragePath(METADATA_STORAGE_PATH) + metadataDirectoryName + "/";
    }

    @Override
    public String getMetadataStoragePath() {
        return getMetadataStoragePath("");
    }

    private String getDocumentStoragePath() {
        return getStoragePath(DOCUMENT_STORAGE_PATH);
    }

    private String getTextStoragePath() {
        String textStoragePath = TEXT_STORAGE_PATH;
        if (textStoragePath == null) {
            throw new IllegalArgumentException("TEXT_DIR is not set in properties file. TEXT_DIR "
                    + "should be set to the path where extracted text files of pdfs should be "
                    + "located");
        }

        return getStoragePath(textStoragePath);

    }

    public String getBaseFulltextPath(Integer repositoryId) {
        String basePath = getDocumentStoragePath() + repositoryId + "/";
        try {
            Files.createDirectories(Paths.get(basePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return basePath;
    }

    private String migrateLegacyPath(final String originalPath) {
        File originalFile = new File(originalPath);
        if (!originalFile.exists()) {
            logger.debug("original path does not exists: {}", originalPath);
            String legacyPath = originalPath.replace("/data/remote", "/data/filesystem");
            File legacyFile = new File((legacyPath));
            if (legacyFile.exists()) {
                logger.debug("legacy path exists: {}", legacyFile);
                try {
                    File parent = new File(originalFile.getParent());
                    if (!Files.exists(parent.toPath())) {
                        parent.mkdirs();
                    }
                    Files.move(legacyFile.toPath(), originalFile.toPath());
                } catch (IOException e) {
                    logger.warn(e.getMessage(), e);
                }
            }
        }
        return originalPath;
    }

    @Override
    public String getPdfPath(Integer articleId, Integer repositoryId) {
        return migrateLegacyPath(getBaseFulltextPath(repositoryId) + articleId.toString() + ".pdf");
    }

    @Override
    public String getExtensionlessDocumentPath(int articleId, int repositoryId) {
        return String.format("%s/%d", getBaseFulltextPath(repositoryId), articleId);
    }

    @Override
    public File getDocumentPath(int articleId, int repositoryId, String extension) {
        return new File(
                migrateLegacyPath(
                        new StringBuilder()
                                .append(this.getExtensionlessDocumentPath(articleId, repositoryId))
                                .append(".")
                                .append(extension)
                                .toString()
                )
        );
    }

    @Override
    public String getTextPath(Integer articleId, Integer repositoryId) {
        String basePath = getTextStoragePath() + repositoryId.toString();
        try {
            Files.createDirectories(Paths.get(basePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return migrateLegacyPath(basePath + "/" + articleId.toString() + ".txt");
    }

    @Override
    public String getDeduplicationReportCachePath(Integer repositoryId) {

        String basePath = BASE_STORAGE_PATH + "/deduplicationReport/";

        try {
            Files.createDirectories(Paths.get(basePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return basePath + repositoryId + ".json";
    }

    @Override
    public boolean deleteFile(String path) {
        File file = new File(path);

        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    @Override
    public boolean moveFile(String srcPath, String dstPath) {
        File file = new File(srcPath);
        File dstFile = new File(dstPath);

        if (file.exists()) {
            return file.renameTo(dstFile);
        }
        return false;
    }

    @Override
    public boolean copyFile(String srcPath, String dstPath) {
        try {
            Files.copy(Paths.get(srcPath), Paths.get(dstPath), StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (IOException e) {
            logger.error("Error during copy file", e);
        }
        return false;
    }

    @Override
    public String getTextPathDeleted(Integer articleId, Integer repositoryId) {
        return migrateLegacyPath(getTextStoragePath() + repositoryId.toString() + "/" + articleId.toString() + "_deleted.txt");
    }

    @Override
    public String getImageBasePath() throws FileNotFoundException {
        if (new File(PREVIEW_STORAGE_PATH).exists()) {
            return PREVIEW_STORAGE_PATH;
        } else {
            throw new FileNotFoundException("Check Filesystem properties file - PREVIEW_STORAGE_PATH folder does not exist: " + PREVIEW_STORAGE_PATH);
        }
    }

    @Override
    public String imageDestinationPathBuilder(Integer documentId, String size) throws FileNotFoundException {
        String fileDestination = getImageBasePath() + size + "/" + documentId.toString() + ".jpg";
        return migrateLegacyPath(fileDestination);
    }

    @Override
    public void storeExtractedTei(Integer articleId, Integer repositoryId, String extractedTeiContent) throws IOException {
        String grobidFullPath = getNormaliseGrobidBasePath();

        String targetDirectory = grobidFullPath + repositoryId;
        String filename = articleId + ".tei.xml";
        this.storeFile(targetDirectory, filename, extractedTeiContent);
    }

    private String getNormaliseGrobidBasePath() {
        String baseStoragePath = this.BASE_STORAGE_PATH;
        String grobidBasePath = this.GROBID_STORAGE_PATH;

        if (!baseStoragePath.endsWith("/")) {
            baseStoragePath += "/";
        }
        String grobidFullPath;
        if (grobidBasePath.startsWith("/")) {
            grobidFullPath = grobidBasePath;
        } else {
            grobidFullPath = baseStoragePath + grobidBasePath;
        }

        if (!grobidFullPath.endsWith("/")) {
            grobidFullPath += "/";
        }
        return grobidFullPath;
    }

    private void storeFile(String directoryPath, String filename, String fileContent) throws IOException {
        System.out.println("Storing in " + directoryPath + " " + filename + " ");

        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
        if (!directoryPath.endsWith("/")) {
            directoryPath += "/";
        }
        FileUtils.writeStringToFile(new File(directoryPath + filename), fileContent, "UTF-8");
    }

    @Override
    public String getExtractedGrobidTeiLocation(Integer articleId, Integer repositoryId) {
        String grobidFullPath = getNormaliseGrobidBasePath();
        String targetDirectory = grobidFullPath + repositoryId;
        String filename = articleId + ".tei.xml";

        return targetDirectory + "/" + filename;
    }

    @Override
    public List<Integer> getTeiFilesIdByRepositoryID(Integer repositoryId) {
        String grobidFullPath = getNormaliseGrobidBasePath();
        File targetDirectory = new File(grobidFullPath + repositoryId);
        File[] listOfFiles = targetDirectory.listFiles();
        List<Integer> listOfFilesIDs = new ArrayList<>();

        for (File listOfFile : listOfFiles) {
            if (listOfFile.isFile()) {
                String name = listOfFile.getName();
                String[] nameParts = name.split("\\.");
                listOfFilesIDs.add(Integer.parseInt(nameParts[0]));
            }
        }
        return listOfFilesIDs;
    }

    @Override
    public List<Integer> getAllTeiFilesIDs(Integer fromRepositoryId) {
        String grobidFullPath = getNormaliseGrobidBasePath();
        File targetDirectory = new File(grobidFullPath);
        File[] grobidFiles = targetDirectory.listFiles();
        Arrays.sort(grobidFiles);
        List<Integer> listOfFilesIDs = new ArrayList<>();
        if (grobidFiles != null) {
            for (File file : grobidFiles) {
                if (file.isDirectory()) {
                    List<Integer> grobidFilesFromRepository = new ArrayList<>();
                    if (Integer.valueOf(file.getName()) > fromRepositoryId) {
                        grobidFilesFromRepository.addAll(getTeiFilesIdByRepositoryID(Integer.parseInt(file.getName())));
                        if (!grobidFilesFromRepository.isEmpty()) {
                            listOfFilesIDs.addAll(grobidFilesFromRepository);
                        }
                    }
                }
            }
        }
        return listOfFilesIDs;
    }

    @Override
    public void updateDocumentStatus(Integer articleId, Integer repositoryId, DeletedStatus deletedStatus) {
        if (deletedStatus == DeletedStatus.DELETED) {
            renameFile(this.getPdfPath(articleId, repositoryId), this.getPdfPathDeleted(articleId, repositoryId));
            renameFile(this.getTextPath(articleId, repositoryId), this.getTextPathDeleted(articleId, repositoryId));
        } else if (deletedStatus == DeletedStatus.ALLOWED) {
            renameFile(this.getPdfPathDeleted(articleId, repositoryId), this.getPdfPath(articleId, repositoryId));
            renameFile(this.getTextPathDeleted(articleId, repositoryId), this.getTextPath(articleId, repositoryId));
        }
    }

    protected boolean renameFile(String oldName, String newName) {
        File fPdfOld = new File(oldName);
        if (fPdfOld.exists()) {
            File fPdfNew = new File(newName);
            fPdfOld.renameTo(fPdfNew);
            return true;
        }
        return false;
    }

    public String getPdfPathDeleted(Integer articleId, Integer repositoryId) {
        return getDocumentStoragePath() + repositoryId + "/" + articleId.toString() + "_deleted.pdf";
    }

    @Override
    public Long getMetadataSize(Integer repositoryId) {
        return this.getLatestMetadataPath(repositoryId).length();
    }

    @Override
    public File getLatestMetadataPath(Integer repositoryId) {
        return new File(getMetadataPath(repositoryId));
    }

    @Override
    /**
     * Gets the path to the latest version of the xml metadata
     */
    public String getMetadataPath(Integer repositoryId) {
        return getMetadataStoragePath() + repositoryId + ".xml";
    }

    /**
     * @param documentId
     * @param repositoryId
     * @param ext
     * @return
     */
    @Override
    public String getMetadataPageDownloadPath(int documentId, int repositoryId, String ext) {
        String path = METADATA_PAGE_DOWNLOAD_DIR + "/" + repositoryId + "/";
        this.makeDirectory(path);
        return path + "/" + documentId + ext;
    }

    /**
     * @param documentId
     * @param repositoryId
     * @return
     */
    @Override
    public String getMetadataPageDownloadPath(int documentId, int repositoryId) {
        return this.getMetadataPageDownloadPath(documentId, repositoryId, ".html");
    }

    @Override
    public String getMetadataPathPart(Integer repositoryId) {
        return getMetadataStoragePath() + repositoryId + "/" + repositoryId + "_part.xml";
    }

    @Override
    public String getIncrementalFolder(Integer repositoryId) {
        return getMetadataStoragePath() + repositoryId + "/";
    }

    @Override
    public String calculatePdfHashValue(Integer articleId, Integer repositoryId) {
        InputStream targetStream = null;
        try {
            String pdfFilePath = this.getPdfPath(articleId, repositoryId);
            File initialFile = new File(pdfFilePath);
            if (initialFile.exists()) {
                targetStream = FileUtils.openInputStream(initialFile);

                return DigestUtils.sha1Hex(targetStream);
            }
        } catch (IOException ex) {
            Logger.getLogger(HardDiskFilesystemDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (targetStream != null) {
                    targetStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(HardDiskFilesystemDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    private String getGrobidExtractedImagesBasePath() {
        return getStoragePath(GROBID_EXTRACTED_IMAGES_PATH);
    }

    @Override
    public String getGrobidExtractedImagesPath(Integer articleId, Integer repositoryId) {
        Integer million = articleId / 1000000;

        Integer hundrethThousand = (articleId % 1000000) / 100000;

        Integer tenthThousand = (articleId % 100000) / 10000;

        Integer thousand = (articleId % 10000) / 1000;

        String basePath = this.getGrobidExtractedImagesBasePath()
                + million + "/"
                + hundrethThousand + "/"
                + tenthThousand + "/"
                + thousand + "/"
                + articleId;

        File baseDir = new File(basePath);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        return basePath;
    }

    public boolean makeDirectory(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
            return true;
        }

        return false;
    }

    // Builds the incremental filename without extension (to be used in public methods)
    private String getIncrementalFilename(Integer repositoryID, Date fromDate, Date untilDate) {
        //TODO: rewrite all this methods
        return DATE_FORMATTER.format(fromDate) + "_" + DATE_FORMATTER.format(untilDate);
    }

    // This method is backward compatible with full harvesting naming schema (if fromDate is null 
    @Override
    public String getMetadataPath(Integer repositoryId, Date fromDate, Date untilDate) {

        if (fromDate == null) // if fromDate is null, full harvesting naming strategy is used
        {
            return getMetadataPath(repositoryId);
        } else // if fromDate is not null, the file will be stored into incremental folder and will use incremental filename strategy
        {
            return getIncrementalFolder(repositoryId) + getIncrementalFilename(repositoryId, fromDate, untilDate) + ".xml";
        }
    }

    // This method is backward compatible with full harvesting naming schema (if fromDate is null) 
    @Override
    public String getMetadataPathPart(Integer repositoryId, Date fromDate, Date untilDate) {

        if (fromDate == null) // if fromDate is null, full harvesting naming strategy is used
        {
            return getMetadataPathPart(repositoryId);
        } else // if fromDate is not null, the file will be stored into incremental folder and will use incremental filename strategy
        {
            return getIncrementalFolder(repositoryId) + getIncrementalFilename(repositoryId, fromDate, untilDate) + "_part.xml";
        }
    }

    @Override
    public void compress(File file) throws IOException {
        if (file.exists()) {
            logger.debug("Compressing " + file.toString());
            File fileZiped = new File(file.getAbsolutePath() + ".gz");
            try (GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(fileZiped))) {
                try (FileInputStream in = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                }
            }
            file.delete();
        } else {
            logger.warn("No file to comress");
        }
    }

    /**
     * Create a path as a string to the current metadata xml storage location
     *
     * @param repositoryId
     * @return A string of the new metadata location path e.g.
     * .../metadata/[repository id]/[YYYY-MM-DD].xml
     */
    @Override
    public File createPathToNewMetadataXmlFile(Integer repositoryId) {
        File path = new File(getIncrementalFolder(repositoryId));
        if (!path.exists()) {
            boolean result = path.mkdir();
            logger.info("Folder created: {}, on path: {}", path.getAbsolutePath(), result);
        } else {
            logger.info("Folder for repository {} already created", repositoryId);
        }
        return new File(getMetadataStoragePath() + repositoryId + "/"
                + DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss").format(LocalDateTime.now()) + ".xml");
    }

    @Override
    public String createNewMetadataPathPart(Integer repositoryId) {
        return getMetadataStoragePath() + repositoryId + "/"
                + DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss").format(LocalDateTime.now()) + "_part.xml";
    }

    @Override
    public void createSymbolicLink(Integer repositoryId, String path) throws IOException {
        Path linkPath = Paths.get(getMetadataStoragePath() + repositoryId + ".xml");
        Path targetPath = Paths.get(path);

        if (Files.exists(linkPath, LinkOption.NOFOLLOW_LINKS)) {
            logger.warn("Removing old {}", linkPath);
            Files.delete(linkPath);
        }
        Files.createSymbolicLink(linkPath, targetPath);
    }

    @Override
    public Pair<LocalDate, LocalDate> getFirstIncrementalDate(Integer repositoryId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String pattern =
                "([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))_([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])).xml";
        try (Stream<Path> walk = Files.walk(Paths.get(getIncrementalFolder(repositoryId)))) {
            return walk
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .filter(str -> str.matches(pattern))
                    .map(this::mapToDateStringPair)
                    .map(p -> mapToDatePair(p, formatter))
                    .min(Comparator.comparing(Pair::getKey))
                    .orElse(null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteDocument(Integer doc, Integer repositoryId) throws IOException {
        String[] filesToRemove = {
                "/data/remote/core/download/metadatapage/$repoId/$id.html",
                "/data/remote/core/filesystem/grobid/$repoId/$id.tei.xml",
                "/data/remote/core/filesystem/pdf/$repoId/$id.pdf",
                "/data/remote/core/filesystem/pdf/$repoId/$id_deleted.pdf",
                "/data/remote/core/filesystem/pdf/$repoId/$id_disabled.pdf",
                "/data/remote/core/filesystem/text/$repoId/$id.txt",
                "/data/remote/core/filesystem/text/$repoId/$id.body",
                "/data/remote/core/filesystem/text/$repoId/$id.cite",
                "/data/remote/core/filesystem/previews/75/$id.jpg",
                "/data/remote/core/filesystem/previews/200/$id.jpg",
                "/data/remote/core/filesystem/previews/400/$id.jpg"
        };

        for (String path : filesToRemove) {
            String finalPath = path.replace("$id", doc.toString()).replace("$repoId", repositoryId.toString());
            Files.deleteIfExists(new File(finalPath).toPath());
        }

    }

    private Pair<String, String> mapToDateStringPair(String raw) {
        String[] dates = raw.replace(".xml", "").split("_");
        if (dates.length != 2) {
            throw new IllegalArgumentException("Unaccepted name: " + raw);
        }
        return Pair.of(dates[0], dates[1]);
    }

    private Pair<LocalDate, LocalDate> mapToDatePair(Pair<String, String> pair, DateTimeFormatter formatter) {
        return Pair.of(
                LocalDate.parse(pair.getKey(), formatter),
                LocalDate.parse(pair.getValue(), formatter));
    }

    @Override
    public File createCrossrefMetadataFile() {
        try {
            final String BASE_PATH = "/data-ext/big-repo-reharvest/crossref/metadata";
            Path basePath = Paths.get(BASE_PATH);

            this.ensureBasePathExists(basePath);

            String filename = "crossref-metadata-" + System.currentTimeMillis() + ".xml";
            File file = Paths.get(BASE_PATH, filename).toFile();

            return this.createAndReturn(file);
        } catch (IOException e) {
            logger.error("I/O exception raised while trying to create temporary file or directory", e);
            return null;
        }
    }

    @Override
    public File createEmptyMalformedReportFile() {
        try {
            final String BASE_PATH = "/data-ext/big-repo-reharvest/crossref/malformed-reports";
            Path basePath = Paths.get(BASE_PATH);

            this.ensureBasePathExists(basePath);

            String filename = "malformed-metadata-report-" + System.currentTimeMillis() + ".csv";
            File file = Paths.get(BASE_PATH, filename).toFile();

            return this.createAndReturn(file);
        } catch (IOException e) {
            logger.error("I/O exception raised while trying to create temporary file or directory", e);
            return null;
        }
    }

    @Override
    public File getLatestCrossrefRecordsMalformedReport() {
        try {
            final String BASE_PATH = "/data-ext/big-repo-reharvest/crossref/malformed-reports";
            Path basePath = Paths.get(BASE_PATH);

            this.ensureBasePathExists(basePath);

            File[] files = basePath.toFile().listFiles();
            if (files == null) {
                throw new IOException();
            }
            return Stream.of(files)
                    .min((o1, o2) -> (int) (o1.lastModified() - o2.lastModified()))
                    .orElseThrow(() -> new RuntimeException(new FileNotFoundException()));
        } catch (IOException e) {
            logger.error("I/O exception raised while trying to get latest Crossref malformed records report", e);
            return null;
        }
    }

    private void ensureBasePathExists(Path basePath) throws IOException {
        if (!Files.exists(basePath)) {
            Files.createDirectories(basePath);
            logger.info("Base folder for Crossref created");
        } else if (!basePath.toFile().isDirectory()) {
            throw new IllegalStateException("Base path for Crossref exists but is not a directory");
        } else {
            logger.info("Base folder for Crossref exists");
        }
    }

    private File createAndReturn(File file) throws IOException {
        boolean fileCreated = file.createNewFile();
        if (fileCreated) {
            logger.info("File successfully created: {}", file.getPath());
        } else {
            logger.warn("File was already created!");
        }
        return file;
    }
}
