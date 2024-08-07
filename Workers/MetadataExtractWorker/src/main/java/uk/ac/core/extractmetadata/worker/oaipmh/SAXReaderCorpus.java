package uk.ac.core.extractmetadata.worker.oaipmh;

import java.util.concurrent.TimeUnit;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.slf4j.LoggerFactory;
import uk.ac.core.common.model.legacy.ArticleMetadata;
import uk.ac.core.common.model.legacy.Corpus;
import uk.ac.core.extractmetadata.worker.oaipmh.XMLParser.XMLRepositoryMetadataReaderSAX;

/**
 * This class exposes XMLRepositoryMetadataReaderSAX in a way that allows SAX to
 * be iterated over
 *
 * @author mk6353
 */
public class SAXReaderCorpus implements Corpus {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SAXReaderCorpus.class);

    ArticleMetadata element;
    XMLRepositoryMetadataReaderSAX XMLreader;

    public void setXMLreader(XMLRepositoryMetadataReaderSAX XMLreader) {
        this.XMLreader = XMLreader;
    }

    public XMLRepositoryMetadataReaderSAX getXMLreader() {
        return XMLreader;
    }

    @Override
    public void start() {
        XMLreader.start();
    }

    @Override
    public ArticleMetadata get() {
        return element;
    }
    DateTime end;

    @Override
    public boolean hasNext() {
        try {

//            DateTime start = new DateTime();
//            Period periodProcess = new Period(end, start);
//            logger.debug("Seconds to process AAA" + periodProcess.getMillis(), this.getClass());
            // wait for reader thread
            boolean acquired = XMLreader.getXMLDocumentHandler().readerWaitSemaphore.tryAcquire(60, TimeUnit.SECONDS);
            if (!acquired) {
                throw new RuntimeException("SaxParser is dead. Stopping the execution");
            }
            // set element
            element = XMLreader.getXMLDocumentHandler().getArticleMetadata();

            // let the parsing thread continue
            XMLreader.getXMLDocumentHandler().parserWaitSemaphore.release();

//            end = new DateTime();
//            Period period = new Period(start, end);
////            logger.debug("Seconds Waiting" + period.getMillis(), this.getClass());
            return (element != null);

        } catch (InterruptedException ex) {
            logger.debug("XML reader interrupted.", this.getClass());
            if (XMLreader.getXMLDocumentHandler().readerWaitSemaphore != null) {
                XMLreader.getXMLDocumentHandler().readerWaitSemaphore.release();
            }
            if (XMLreader.getXMLDocumentHandler().parserWaitSemaphore != null) {
                XMLreader.getXMLDocumentHandler().parserWaitSemaphore.release();
            }
            XMLreader.interrupt();
            return false;
        }
    }
}

