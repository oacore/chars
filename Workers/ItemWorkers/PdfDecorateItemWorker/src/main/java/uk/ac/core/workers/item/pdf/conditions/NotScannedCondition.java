package uk.ac.core.workers.item.pdf.conditions;

import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public class NotScannedCondition implements Callable<Boolean> {

    private String filePath;
    private static final Logger logger = LoggerFactory.getLogger(NotScannedCondition.class);

    public NotScannedCondition(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Boolean call() {
        int count = 0;
        int numOfPages = 0;
        try {
            PDDocument doc = PDDocument.load(new File(filePath));
            numOfPages = doc.getNumberOfPages();
            for (PDPage page: doc.getPages()) {
                PDResources resource = page.getResources();
                for (COSName xObjectName: resource.getXObjectNames()) {
                    PDXObject xObject = resource.getXObject(xObjectName);
                    if (xObject instanceof PDImageXObject) {
                        count++;
                    }
                    else {
                        return true;
                    }
                }
            }
            doc.close();
        } catch (IOException e) {
            logger.error("Exception: {}", e.getMessage());
        }
        return count != numOfPages;
    }
}