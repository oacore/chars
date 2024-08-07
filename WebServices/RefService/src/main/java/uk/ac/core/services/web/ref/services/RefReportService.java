package uk.ac.core.services.web.ref.services;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.core.crossref.CrossrefService;
import uk.ac.core.filesystem.services.FilesystemDAO;
import uk.ac.core.services.web.ref.database.RefReportDAO;
import uk.ac.core.services.web.ref.model.FullTextReportDTO;
import uk.ac.core.services.web.ref.model.RefReportDTO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Service
public class RefReportService {

    private static final String FILE_PATH_RESULT = "results/";
    private static final String FILE_PATH_DOIS = "";
    private static String REF_DOIS = "Q_Outputs3ScopusDoiOrTitleNoScore0.DOIs-nodup.txt";
    private static String FULL_TEXT_PATH_RESULT = "full_text_dataset.csv";
    private static String FULL_TEXED_MOVED = "full_text_results/";


    @Autowired
    private RefReportDAO refReportDAO;

    @Autowired
    private CrossrefService crossrefService;

    @Autowired
    private FilesystemDAO filesystemDAO;

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(RefReportService.class);

    public void run() {
        batchRun();
    }

    private void batchRun() {
        int BATCH = 500;
        List<String> dois = readFileAndGetDois();
        logger.info("Fetched {} dois", dois.size());
        IntStream.range(0, (dois.size() + BATCH - 1) / BATCH)
                .mapToObj(i -> dois.subList(i * BATCH, Math.min(dois.size(), (i + 1) * BATCH)))
                .parallel()
                .map(this::getDataAndWrite)
                .peek(d -> logger.info("Fetched {} entries", d.size()))
                .forEach(refReportDTOS -> logger.info("done"));

    }

    private List<RefReportDTO> getDataAndWrite(List<String> doiBatch) {
        logger.info("Processing a list with " + doiBatch.size());
        List<RefReportDTO> refReportDTOS = doiBatch.stream()
                .flatMap(doi -> getReportInfo(doi).stream())
                .collect(Collectors.toList());
        writeIntoTheFile(refReportDTOS);
        return refReportDTOS;
    }

    public List<RefReportDTO> getReportInfo(String doi) {
        logger.info("Start getting info for " + doi);
        List<RefReportDTO> reportInfo;
        reportInfo = refReportDAO.getMuccReportData(doi);
        for (RefReportDTO reportDTO : reportInfo) {
            if (reportDTO.getPublicationDateCrossref() == null) {
                try {
                    Date date = crossrefService.downloadPublicationDate(doi);
                    if (date != null) {
                        reportDTO.setPublicationDateCrossref(date);
                    }
                } catch (Exception e) {
                    logger.warn("Error while downloading publication date ", e);
                }
            }
        }


        reportInfo.addAll(refReportDAO.getReportData(doi));
        logger.debug(String.valueOf(reportInfo.size()));
        Map<Integer, List<RefReportDTO>> reportInfo1 = reportInfo.stream().collect(Collectors.groupingBy(RefReportDTO::getIdDocument));
        reportInfo = reportInfo1
                .entrySet().stream()
                .filter(e -> {
                    RefReportDTO result = e.getValue().get(0);
                    return result.getDoi().trim().equalsIgnoreCase(doi.trim());
                })
                .map(e -> {
                    RefReportDTO result = e.getValue().get(0);

                    for (RefReportDTO dto : e.getValue()) {
                        if (dto.getPublicationDate() != null) {
                            result.setPublicationDate(dto.getPublicationDate());
                        }
                    }

                    return result;
                }).collect(Collectors.toList());

        return reportInfo;

    }

    private synchronized void writeIntoTheFile(List<RefReportDTO> reportInfo) {
        logger.info("Start writing to the file {} entries", reportInfo.size());
        String filename = FILE_PATH_RESULT +
                UUID.randomUUID().toString()
                + ".csv";
        try {
            FileWriter fileWriter = new FileWriter(filename, false);

            for (RefReportDTO reportDTO : reportInfo) {
                fileWriter.write(reportDTO.toString());
                fileWriter.write("\n");
            }
            fileWriter.close();

            logger.info("Data successfully wrote");
        } catch (IOException e) {
            logger.error("Error during write data to file", e);
        }

    }

    private List<String> readFileAndGetDois() {
        logger.info("Start reading the file");
        List<String> dois = new ArrayList<>();

        File file = new File(FILE_PATH_DOIS + "ref_dois.txt");

        try (Scanner reader = new Scanner(file)) {
            while (reader.hasNextLine()) {
                dois.add(reader.nextLine());
            }
        } catch (FileNotFoundException e) {
            logger.error("Error during opening the file", e);
        }
        logger.info("Number of doi {}", dois.size());
        return dois;
    }

    public void generateFullTextReport() throws IOException {
        Long start = System.currentTimeMillis();
        List<String> dois = getDoisFromFile();
        Integer iterationCount = 0;
        if(dois != null && !dois.isEmpty()){
            try (FileWriter fileWriter = new FileWriter(FULL_TEXT_PATH_RESULT, false)) {
                for(String doi: dois){
                    processOneDoi(doi, fileWriter);

//                    if(iterationCount > 1000){
//                        logger.info("Processing took {} ms", System.currentTimeMillis() - start);
//                        return;
//                    }
                    iterationCount++;
                    logger.info("Process {} doi", iterationCount);
                }
            } catch (IOException e){
                logger.error("Error during write data to file", e);
            }
        }
        logger.info("Processing took {} ms", System.currentTimeMillis() - start);
    }

    private void processOneDoi(String doi, FileWriter fileWriter) throws IOException {
        Long start = System.currentTimeMillis();
        List<FullTextReportDTO> fullTextReportDTOSByDoi = refReportDAO.getFullTextReportData(doi);
        if (fullTextReportDTOSByDoi.isEmpty()) {
            logger.info("No documents found for doi {}", doi);
            return;
        }

        logger.info("Were found {} documents for doi {}", fullTextReportDTOSByDoi.size(), doi);

        Optional<FullTextReportDTO> biggestFullTextFile = fullTextReportDTOSByDoi.stream()
                .peek(f -> f.setFulltextFileSize(getFullTextFileSize(f)))
                .filter(f -> f.getFulltextFileSize() > 0)
                .limit(3)
                .max(Comparator.comparingLong(FullTextReportDTO::getFulltextFileSize));

        if (biggestFullTextFile.isPresent()) {
            FullTextReportDTO fullTextReportDTO = biggestFullTextFile.get();
            logger.info("Full text file exists for document {} in repo {}",
                    fullTextReportDTO.getIdDocument(), fullTextReportDTO.getIdRepository());
            fullTextReportDTO.setFullTextAvailable(true);
            fileWriter.write(fullTextReportDTO.toString());
            fileWriter.write("\n");

            String destinationPath = FULL_TEXED_MOVED + fullTextReportDTO.getIdDocument() + ".txt";
            Long beforeCopy = System.currentTimeMillis();
            String originalPath = filesystemDAO.getTextPath(fullTextReportDTO.getIdDocument(), fullTextReportDTO.getIdRepository());
            filesystemDAO.copyFile(originalPath, destinationPath);
            logger.info("Copying take {} ms", System.currentTimeMillis() - beforeCopy);
            logger.info("Full text file was moved");
        } else {
            FullTextReportDTO fullTextReportDTO = fullTextReportDTOSByDoi.get(fullTextReportDTOSByDoi.size() - 1);
            logger.info("No file exist for doi {}. Saving default value", doi);
            fullTextReportDTO.setFullTextAvailable(false);
            fileWriter.write(fullTextReportDTO.toString());
            fileWriter.write("\n");
        }

        logger.info("Processing doi takes {} ms", System.currentTimeMillis() - start);
    }

    private long getFullTextFileSize(FullTextReportDTO fullTextReportDTO) {
        String originalPath = filesystemDAO.getTextPath(fullTextReportDTO.getIdDocument(), fullTextReportDTO.getIdRepository());

        try {
            return Files.size(Paths.get(originalPath));
        } catch (IOException e) {
            logger.warn("Unable retrieve file size");
            return 0L;
        }
    }

    private List<String> getDoisFromFile() throws IOException {
        try (Stream<String> stream = Files.lines(Paths.get(REF_DOIS), StandardCharsets.UTF_8)) {
            return stream.collect(Collectors.toList());
        }
    }
}
