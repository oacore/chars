package uk.ac.core.metadatadownloadworker.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.core.filesystem.services.FilesystemDAO;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

@RestController
@RequestMapping("/test-old-file")
public class OldRepositoryFilesSupportController {
    private final static Logger logger = LoggerFactory.getLogger(OldRepositoryFilesSupportController.class);

    private final FilesystemDAO filesystemDAO;

    @Autowired
    public OldRepositoryFilesSupportController(FilesystemDAO filesystemDAO) {
        this.filesystemDAO = filesystemDAO;
    }

    @GetMapping("create-simblink")
    public ResponseEntity<String> createSimblink() {
//        File storageFolder = new File(filesystemDAO.getMetadataStoragePath());
//
//        int counter = 0;
//        for (File file : storageFolder.listFiles()) {
//            if(!file.getName().endsWith(".xml")) {
//                process
//            }
//        }

        return ResponseEntity.ok("SUCCESS");
    }

    @GetMapping("move")
    public ResponseEntity<String> move() {
        File storageFolder = new File(filesystemDAO.getMetadataStoragePath());

        int counter = 0;
        for (File file : storageFolder.listFiles()) {
            if(file.getName().endsWith(".xml") && !file.getName().endsWith("_part.xml")){
                try {
                    processOneRepository(file);
                    counter++;
                } catch (Exception e) {
                    logger.error("Error while processing file " + file.getName(), e);
                }
            }
        }

        return ResponseEntity.ok("Success. Processed " + counter + " files");
    }

    private void processOneRepository(File file) throws IOException {
        String repositoryId = file.getName().replace(".xml", "");

        File repositoryFolder = new File(filesystemDAO.getIncrementalFolder(Integer.parseInt(repositoryId)));
        String newDestination = repositoryFolder.getAbsolutePath() + "/" +
                new SimpleDateFormat("yyyy-MM-dd").format(file.lastModified()) + ".xml";
        if(repositoryFolder.exists()) {
            filesystemDAO.moveFile(file.getAbsolutePath(), newDestination);
            filesystemDAO.createSymbolicLink(Integer.parseInt(repositoryId), newDestination);
            filesystemDAO.compress(new File(newDestination));
        } else {
            repositoryFolder.mkdir();
            filesystemDAO.moveFile(file.getAbsolutePath(), newDestination);
            filesystemDAO.createSymbolicLink(Integer.parseInt(repositoryId), newDestination);
        }
    }
}