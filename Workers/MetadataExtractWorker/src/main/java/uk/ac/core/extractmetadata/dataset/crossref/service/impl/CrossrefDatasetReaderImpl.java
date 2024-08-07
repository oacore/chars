package uk.ac.core.extractmetadata.dataset.crossref.service.impl;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.ac.core.extractmetadata.dataset.crossref.exception.CrossrefDatasetLockException;
import uk.ac.core.extractmetadata.dataset.crossref.service.CrossrefDatasetReader;

import javax.annotation.PostConstruct;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

@Service
public class CrossrefDatasetReaderImpl implements CrossrefDatasetReader {
    private static final Logger log = LoggerFactory.getLogger(CrossrefWorkServiceImpl.class);
    private static final String WORKING_DIR = "/data-ext/crossref-data/crossref-working-dir/";
    private static final String CHECKPOINTS_FOLDER = WORKING_DIR + "checkpoints/";
    private static final String TMP_FOLDER = WORKING_DIR + "tmp/";
    private static final String LOCK_FOLDER = WORKING_DIR + "lock/";
    private static final String GZ_ENTRIES_FILENAME = "tar-entries.txt";

    @Value("${crossref-dataset:false}")
    private Boolean datasetMode;

    private List<String> gzEntries;
    private List<String> gzEntriesProcessed;
    private List<String> gzEntriesInProgress;

    public CrossrefDatasetReaderImpl() {
    }

    @PostConstruct
    private void initialiseWorkspace() {
        log.info("Dataset mode: {}", this.datasetMode);
        if (this.datasetMode) {
            this.prepareWorkingDir();
            this.gzEntries = this.loadGzFilenames();
            this.gzEntriesProcessed = this.readCheckpoints();
        }
    }

    private void prepareWorkingDir() {
        // make sure `checkpoints` and `tmp` folders exist
        this.createOrFixPath(Paths.get(CHECKPOINTS_FOLDER));
        this.createOrFixPath(Paths.get(TMP_FOLDER));
        this.createOrFixPath(Paths.get(LOCK_FOLDER));
    }

    private void createOrFixPath(Path path) {
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Path has been created - {}", path.toFile().getPath());
            } else if (!Files.isDirectory(path)) {
                log.info("Oops! Path exists but is not a directory - {}", path.toFile().getPath());
                log.info("Fixing ...");
                Files.delete(path);
                Files.createDirectories(path);
                log.info("Done");
            } else {
                log.info("Path already exists - {}", path.toFile().getPath());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> readCheckpoints() {
        try (Stream<Path> checkpoints = Files.list(Paths.get(CHECKPOINTS_FOLDER))) {
            long start, end;
            start = System.currentTimeMillis();
            log.info("Reading checkpoints ...");
            final List<String> processedFilenames = new ArrayList<>();
            checkpoints
                    .filter(f -> f.toFile().getPath().endsWith(".txt"))
                    .map(path -> { // each file is a TXT file containing the name of processed entry
                        byte[] bytes;
                        try {
                            bytes = Files.readAllBytes(path);
                            return new String(bytes);
                        } catch (IOException e) {
                            log.warn("Failed to read checkpoint {}", path.toFile().getPath());
                            return null;
                        }
                    })
                    .forEach(entryName -> {
                        if (entryName != null) {
                            processedFilenames.add(entryName);
                        }
                    });
            end = System.currentTimeMillis();
            log.info("Done in {} ms", end - start);
            return processedFilenames;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> loadGzFilenames() {
        try {
            long start, end;
            start = System.currentTimeMillis();
            log.info("Dumping TAR entries ...");
            Scanner scanner = new Scanner(new File(WORKING_DIR + GZ_ENTRIES_FILENAME));
            List<String> filenames = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String entryName = scanner.nextLine();
                filenames.add(entryName);
            }

            end = System.currentTimeMillis();
            log.info("Done in {} ms", end - start);
            return filenames;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getNextEntryName() throws CrossrefDatasetLockException {
        this.gzEntriesInProgress = this.readLocks();
        final List<String> excludedEntries = new ArrayList<>();
        excludedEntries.addAll(this.gzEntriesProcessed);
        excludedEntries.addAll(this.gzEntriesInProgress);
        return this.gzEntries.stream()
                .filter(entry -> !excludedEntries.contains(entry))
                .sorted()
                .findFirst().orElse(null);
    }

    private List<String> readLocks() throws CrossrefDatasetLockException {
        List<String> lockedEntries = new ArrayList<>();

        File[] locks = Paths
                .get(LOCK_FOLDER)
                .toFile()
                .listFiles(pathname -> pathname.getName().endsWith(".json.lock"));

        if (locks == null) {
            throw new CrossrefDatasetLockException("Failed to read locks from " + LOCK_FOLDER);
        }

        for (File lock : locks) {
            String entryName = "April 2023 Public Data File from Crossref/" +
                    lock.getName().replace(".lock", "") +
                    ".gz";
            lockedEntries.add(entryName);
        }

        return lockedEntries;
    }

    private String getLockPath(String entryName) {
        return LOCK_FOLDER + FilenameUtils.getBaseName(entryName) + ".lock";
    }

    @Override
    public File extractEntry(String entryName) throws IOException {
        final String gzPath = WORKING_DIR + entryName;
        File gzFile = new File(gzPath);
        File jsonFile = new File(TMP_FOLDER + FilenameUtils.getBaseName(entryName));

        GZIPInputStream gis = null;
        FileOutputStream jsonFos = null;

        try {

            gis = new GZIPInputStream(Files.newInputStream(gzFile.toPath()));
            jsonFos = new FileOutputStream(jsonFile);
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gis.read(buffer)) != -1) {
                jsonFos.write(buffer, 0, len);
            }

        } catch (ZipException zipEx) {
            log.error("Entry is not a valid GZIP file! - {}", entryName);
            log.info("Reporting entry {} as corrupted", entryName);
            this.checkpoint(entryName, "corrupted"); // skip corrupted file
            throw zipEx;
        } catch (IOException e) {
            log.error("I/O exception raised while reading entry from TAR - {}", entryName);
            log.error("", e);
            throw e;
        } finally {
            this.closeResources(gis, jsonFos);
        }

        return jsonFile;
    }

    private void closeResources(Closeable... closeables) {
        long start, end;
        start = System.currentTimeMillis();
        log.info("Closing resources ...");
        for (Closeable closeable : closeables) {
            if (closeable != null) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        end = System.currentTimeMillis();
        log.info("Done in {} ms", end - start);
    }

    @Override
    public void checkpoint(String entryName) {
        this.checkpoint(entryName, "processed");
    }

    private void checkpoint(String entryName, String type) {
        try {
            String checkpointName = type + "_" + System.currentTimeMillis() + ".txt";
            File checkpoint = new File(CHECKPOINTS_FOLDER + checkpointName);
            checkpoint.createNewFile();

            Files.write(checkpoint.toPath(), entryName.getBytes());

            log.info("Checkpoint saved - {}", checkpoint.getPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void rereadCheckpoints() {
        this.gzEntriesProcessed.clear();
        this.gzEntriesProcessed = this.readCheckpoints();
    }

    @Override
    public File setLock(String entryName) throws CrossrefDatasetLockException {
        try {
            String lockPath = this.getLockPath(entryName);
            File lock = new File(lockPath);
            if (lock.createNewFile()) {
                log.info("Lock on entry '{}' successfully created", entryName);
            } else {
                throw new CrossrefDatasetLockException("Lock on entry '" + entryName + "' already exists");
            }
            return lock;
        } catch (IOException e) {
            log.error("I/O exception occurred while creating lock on entry '{}'", entryName);
            log.error("", e);
            throw new CrossrefDatasetLockException(e);
        }
    }

    @Override
    public void releaseLock(File lock) throws CrossrefDatasetLockException {
        try {
            Files.delete(lock.toPath());
            log.info("Lock {} released", lock.getName());
        } catch (IOException e) {
            throw new CrossrefDatasetLockException("Failed to release lock " + lock.getName(), e);
        }
    }
}
