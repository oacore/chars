package uk.ac.core.workers.item.pdf.conditions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Callable;

public class DocumentDecorationCondition implements Callable<Boolean> {
    String filePath;
    
    private static final Logger logger = LoggerFactory.getLogger(DocumentDecorationCondition.class);

    public DocumentDecorationCondition(String filePath) {
        this.filePath = filePath;

    }

    @Override
    public Boolean call() throws IOException {
        long start, end;

        start = System.currentTimeMillis();
        Boolean filesizeCondition = new FileSizeCondition(filePath).call();
        end = System.currentTimeMillis();
        logger.info("FilesizeCondition: {}, {} ms", filesizeCondition.toString(), end - start);

        start = System.currentTimeMillis();
        Boolean pageOrientationCondition = new PageOrientationCondition(filePath).call();
        end = System.currentTimeMillis();
        logger.info("PageOrientationCondition: {}, {} ms", pageOrientationCondition.toString(), end - start);

        start = System.currentTimeMillis();
        Boolean alreadyDecoratedCondition = new NotDecoratedCondition(filePath).call();
        end = System.currentTimeMillis();
        logger.info("NotDecoratedCondition: {}, {} ms", alreadyDecoratedCondition.toString(), end - start);

        start = System.currentTimeMillis();
        Boolean notScannedPdfCondition = new NotScannedCondition(filePath).call();
        end = System.currentTimeMillis();
        logger.info("NotScannedCondition: {}, {} ms", notScannedPdfCondition.toString(), end - start);

        return filesizeCondition && pageOrientationCondition && alreadyDecoratedCondition && notScannedPdfCondition;
    }
}
