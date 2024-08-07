package uk.ac.core.workers.item.pdf.conditions;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public class PageOrientationCondition implements Callable<Boolean> {
    String filePath;

    PageOrientationCondition(String filePath) {
        this.filePath = filePath;
    }

    private boolean isPageLandscape(PDPage page) {
        return page.getMediaBox().getWidth() >= page.getMediaBox().getHeight();
    }

    @Override
    public Boolean call() throws IOException {
        System.out.println("Testing landscape");
        File file = new File(filePath);
        try (PDDocument document = PDDocument.load(file)) {
            PDPage targetPage = document.getPage(0);
            if (document.getNumberOfPages() > 1) {
                targetPage = document.getPage(1);
            }
            // We test second page primarily because the fist page could be injected by the data provider,
            // so it could have different dimensions from other ones.
            // However, if the document is a single page article without prepended title page, we check that only page.
            return targetPage != null ? !isPageLandscape(targetPage) : !isPageLandscape(targetPage);
        }
    }
}
