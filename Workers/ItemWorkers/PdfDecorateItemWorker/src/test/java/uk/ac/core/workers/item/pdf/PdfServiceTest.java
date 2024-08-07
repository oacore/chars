package uk.ac.core.workers.item.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;
import uk.ac.core.common.model.legacy.RepositoryDocument;
import uk.ac.core.dataprovider.logic.dto.DataProviderBO;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PdfServiceTest {
    String resolveLocalFile(Integer documentId) {
        final String fileName = documentId + ".pdf";
        final ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());
        return file.getAbsolutePath();
    }

    String resolveOutputFilePath(String inputFilePath, String suffix) {
        return inputFilePath.replace(".pdf", String.format("_%s.pdf", suffix));
    }

    // White-box creating annotation context. We literally pick variables that AnnotationContext constructor uses.
    static AnnotationContext createContext(Integer documentId, String repositoryName) {
        RepositoryDocument repositoryDocument = new RepositoryDocument();
        repositoryDocument.setIdDocument(documentId);
        DataProviderBO dataProvider = new DataProviderBO();
        dataProvider.setName(repositoryName);
        return new AnnotationContext(repositoryDocument, dataProvider);
    }

    void logFileSize(String inputFilePath, String outputFilePath) {
        File inputFile = new File(inputFilePath);
        File outputFile = new File(outputFilePath);
        long diff = outputFile.length() - inputFile.length();
        System.out.printf("File size increased by %d bytes (%d.2%%)%n", diff, diff / inputFile.length() * 100);
    }

    public void runHeaderInjecting(String inputFilePath, String outputFilePath, AnnotationContext context) throws IOException {
        System.out.printf("Running test on %s%n", inputFilePath);
        try (PDDocument pdDocument = PdfDecoratingService.loadDocument(inputFilePath)) {
            PDPage firstPage = pdDocument.getPage(0);
            PdfDecoratingService.injectHeader(pdDocument, firstPage, context);
            PdfDecoratingService.saveDocument(pdDocument, outputFilePath);
            System.out.printf("Result saved at %s%n", outputFilePath);
            logFileSize(inputFilePath, outputFilePath);
        }
    }

    public void runHeaderInjecting(Integer documentId, String repositoryName, String testSuffix) throws IOException {
        final String inputFilePath = resolveLocalFile(documentId);
        final String outputFilePath = resolveOutputFilePath(inputFilePath, testSuffix);
        final AnnotationContext context = createContext(documentId, repositoryName);
        runHeaderInjecting(inputFilePath, outputFilePath, context);
    }

    public void runHeaderInjecting(Integer documentId, String repositoryName) throws IOException {
        runHeaderInjecting(documentId, repositoryName, "undefined");
    }

    @Test
    public void testBasicHeaderInjecting() throws IOException {
        final Integer documentId = 131304593;
        final String repositoryName = "Open Research Online";

        runHeaderInjecting(documentId, repositoryName, "base");
    }

    @Test
    public void testDocumentWithImageHeader() throws IOException {
        final Integer documentId = 95757;
        final String repositoryName = "LSE Research Online";

        runHeaderInjecting(documentId, repositoryName, "image_header");
    }

    @Test
    public void testLongRepositoryName() throws IOException {
        final Integer documentId = 131304593;
        final String repositoryName = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat";

        runHeaderInjecting(documentId, repositoryName, "long_name");
    }


    @Test
    public void testUnicode() throws IOException {
        final Integer documentId = 131304593;

        // The longest name picked from our database at 11 Feb 2021
        final String repositoryName = "Institute of Philosophy, Ivane Javakhishvili Tbilisi State University: E-Journals / ივანე ჯავახიშვილის სახელობის თბილისის სახელმწიფო უნივერსიტეტი: ფილოსოფიის სასწავლო–სამეცნიერო ინსტიტუტი";

        runHeaderInjecting(documentId, repositoryName, "unicode_name");
    }

    @Test
    public void testEncryptedReturnsIOException()  {
        try {
            final Integer documentId = 333569;
            final String repositoryName = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat";

            runHeaderInjecting(documentId, repositoryName, "long_name");
        } catch (IOException e){


        }
    }

    @Test
    public void testIsDecorated() throws IOException {
        final Integer documentId = 131304593;

        // The longest name picked from our database at 11 Feb 2021
        final String repositoryName = "Institute of Philosophy, Ivane Javakhishvili Tbilisi State University: E-Journals / ივანე ჯავახიშვილის სახელობის თბილისის სახელმწიფო უნივერსიტეტი: ფილოსოფიის სასწავლო–სამეცნიერო ინსტიტუტი";

        final String inputFilePath = resolveLocalFile(documentId);
        final String outputFilePath = resolveOutputFilePath(inputFilePath, "undefined");
        final AnnotationContext context = createContext(documentId, repositoryName);
        runHeaderInjecting(inputFilePath, outputFilePath, context);
        try (PDDocument pdDocumentOut = PdfDecoratingService.loadDocument(outputFilePath);
             PDDocument pdDocumentIn = PdfDecoratingService.loadDocument(inputFilePath)) {
            assertTrue(PdfDecoratingService.isDecorated(pdDocumentOut));
            assertFalse(PdfDecoratingService.isDecorated(pdDocumentIn));
        }
    }
}