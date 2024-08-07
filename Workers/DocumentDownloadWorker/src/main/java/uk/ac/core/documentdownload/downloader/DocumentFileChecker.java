package uk.ac.core.documentdownload.downloader;

import org.slf4j.LoggerFactory;
import uk.ac.core.common.util.FuzzyMatcher;
import uk.ac.core.textextraction.TextExtractorService;
import uk.ac.core.textextraction.extractor.TextExtractorType;

import java.io.*;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author mc26486
 */
public class DocumentFileChecker {
    /**
     * The maximum file size to check, in bytes
     * This avoids a Out of Memory Exception
     */
    private static final long TITLE_CHECKING_LIMIT = 31457280;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DocumentFileChecker.class);

    private static final Set<String> parsableContentTypes = new HashSet<>(Arrays.asList(
            "text/html",
            "text/plain",
            "text/html;charset=utf-8"));

    /**
     * Checks if content type is parsable -- that is if URLs can be retrieved
     * from the file (e.g. HTML etc.).
     *
     * @param contentType
     *
     * @return
     */
    public static boolean isParsableFile(String contentType) {
        for (String parsableContentType : DocumentFileChecker.parsableContentTypes) {
            if (contentType.toLowerCase().contains(parsableContentType)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a document is valid.
     *
     * @param data data as array of bytes
     *
     * @return true if valid
     */
    private static boolean isDocumentValid(byte[] data) {

        try {
            return data.length >= 5 && (isPdf(data) || isDoc(data));
        } catch (Exception ex) {
            return false;
        }
    }

    public static boolean isPdf(byte[] data) {
        return (data[0] == (byte) 0x25) // %
                && (data[1] == (byte) 0x50) // P
                && (data[2] == (byte) 0x44) // D
                && (data[3] == (byte) 0x46) // F
                && (data[4] == (byte) 0x2D); // -
    }

    public static boolean isDoc(byte[] data) throws IOException {
        //currently only doc
        final int[] docBytes = new int[] { 0xd0, 0xcf, 0x11, 0xe0, 0xa1, 0xb1, 0x1a, 0xe1 };

        try(ByteArrayInputStream stream = new ByteArrayInputStream(data)) {
            boolean isWord = false;

            for (int i = 0; i < 8; i++) {
                isWord =  stream.read() == docBytes[i];
            }

            return isWord;
        }
    }

    /**
     * Checks if file is valid based on the content type (e.g. for PDF it checks
     * if the file really is a PDF).
     *
     * @param file
     * @param contentType
     *
     * @return
     */
    public static boolean isFileValid(byte[] file, String contentType) {

        boolean isDocumentValid = DocumentFileChecker.isDocumentValid(file);
        if (contentType.contains("openxmlformats")){
            isDocumentValid=true;
        }
        if (isDocumentValid) {
            logger.info("File is valid PDF or Word file.");
        } else {
            logger.info("Content type " + contentType + " is not a PDF or Word.", DocumentFileChecker.class);
        }

        return isDocumentValid;
    }

    /**
     * Gets the extension based on the file contents
     *
     * @todo convert to ENUM or provide some other system for determining extensions
     *
     * @param file
     * @param contentType
     *
     * @return
     */
    public static String getExtension(byte[] file, String contentType) {
        if (isPdf(file)) {
            return "pdf";
        }
        try {
            if (isDoc(file)) {
                return "doc";
            }
            else {
                return "docx";
            }
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
        return "";
    }

    /**
     * Checks if file is valid based on the content type (e.g. for PDF it checks
     * if the file really is a PDF).
     *
     * @param filePath
     *
     * @return
     */
    public static boolean isFileValid(String filePath) {
        Boolean result = false;
        byte[] buffer = new byte[100];
        try (InputStream is = new FileInputStream(filePath)) {
            if (is.read(buffer) == buffer.length) {
                result = DocumentFileChecker.isDocumentValid(buffer);
            }
        } catch (FileNotFoundException ex) {
            logger.error(ex.getMessage());
        } catch (IOException ex) {
            logger.error((ex.getMessage()));
        }

        return result;
    }


    public static boolean isTitleMatching(String titleToCheck, String filePath, String extension, Long contentSize) throws IOException {
        logger.debug("testing if " + titleToCheck + " is the title of the document just downloaded:" + filePath);

        if (contentSize > TITLE_CHECKING_LIMIT) {
            logger.info("File size [{} bytes] exceeds the limit of {} bytes", contentSize, TITLE_CHECKING_LIMIT);
            // we cannot be sure that this PDF is our paper so return false
            return false;
        }

        long startTime = System.nanoTime();

        if (titleToCheck == null || titleToCheck.isEmpty()) {
            return false;
        }

        String page;
        long pushBackSize = 20 * 1024 * 1024;// 20 Mb
        System.setProperty("org.apache.pdfbox.baseParser.pushBackSize", "" + pushBackSize);
        try (TextExtractorService textService = new TextExtractorService(Paths.get(filePath), TextExtractorType.valueOf(extension.toUpperCase()))) {
            page = textService.getTextContainingTitle();
        }

        String processedTitle = processString(titleToCheck);

        String processedPage = processString(page);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000000;  //divide by 1000000 to get milliseconds.
        logger.info("Time taken for PDF Box to parse file: " + duration);
        if (!processedPage.contains(processedTitle)) {
            boolean fuzzyMatch = FuzzyMatcher.fuzzyStringMatch(processedPage, processedTitle, 0.10);
            logger.info("Checking title: FUZZY match score: " + fuzzyMatch);
            return fuzzyMatch;
        } else {
            logger.info("Checking title: EXACT match {}", titleToCheck);
        }

        return true;
    }

    /**
     * Removes all no alpha numeric characters of any language
     */
    private static String removeAllNonAlphaNumerics(String string) {
        // this replaceAll("[^a-zA-Z0-9]", " ") would work only for english characters
        return string.replaceAll("[^\\p{L}\\p{Nd}]+", "");
    }

    /**
     * Process a string and clear it from all special characters, whitespaces
     * (anything else than alphanumeric), trim it and lowercase it
     *
     * @param string
     *
     * @return
     */
    private static String processString(String string) {
        String lowered = string.toLowerCase();
        String alphaNumericsRemoved = removeAllNonAlphaNumerics(lowered);
        String trimmed = alphaNumericsRemoved.trim();
        return trimmed;
    }
}
