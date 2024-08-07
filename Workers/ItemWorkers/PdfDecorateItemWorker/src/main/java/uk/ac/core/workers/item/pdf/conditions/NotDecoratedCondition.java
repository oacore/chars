package uk.ac.core.workers.item.pdf.conditions;

import org.apache.pdfbox.pdmodel.PDDocument;
import uk.ac.core.workers.item.pdf.PdfDecoratingService;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public class NotDecoratedCondition implements Callable<Boolean> {


    private String filePath;

    NotDecoratedCondition(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Boolean call() {
        System.out.println("Testing decoration");
        try (PDDocument document = PdfDecoratingService.loadDocument(filePath)) {
            return !PdfDecoratingService.isDecorated(document);
        } catch (IOException e) {
            return false;
        }
    }

}
