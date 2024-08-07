package uk.ac.core.extractmetadata.worker.oaipmh.XMLParser;
import org.slf4j.LoggerFactory;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.*;
/**
 * @author Giorgio Basile
 * @since 12/04/2017
 */
public class ProgressInputStream extends FilterInputStream {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProgressInputStream.class);
    private final PropertyChangeSupport propertyChangeSupport;
    private final long maxNumBytes;
    private volatile long totalNumBytesRead;
    private volatile float percentage;

    public ProgressInputStream(InputStream in, long maxNumBytes) {
        super(in);
        this.propertyChangeSupport = new PropertyChangeSupport(this);
        this.maxNumBytes = maxNumBytes;
    }

    public long getMaxNumBytes() {
        return maxNumBytes;
    }
    public long getTotalNumBytesRead() {
        return totalNumBytesRead;
    }
    public float getPercentage() {
        return percentage;
    }
    public void addPropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.addPropertyChangeListener(l);
    }
    public void removePropertyChangeListener(PropertyChangeListener l) {
        propertyChangeSupport.removePropertyChangeListener(l);
    }
    @Override
    public int read() throws IOException {
        int b = super.read();
        updateProgress(1);
        return b;
    }
    @Override
    public int read(byte[] b) throws IOException {
        return (int)updateProgress(super.read(b));
    }
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return (int)updateProgress(super.read(b, off, len));
    }
    @Override
    public long skip(long n) throws IOException {
        return updateProgress(super.skip(n));
    }
    @Override
    public void mark(int readlimit) {
        throw new UnsupportedOperationException();
    }
    @Override
    public void reset() throws IOException {
        throw new UnsupportedOperationException();
    }
    @Override
    public boolean markSupported() {
        return false;
    }
    private long updateProgress(long numBytesRead) {
        if (numBytesRead > 0) {
            long oldTotalNumBytesRead = this.totalNumBytesRead;
            this.totalNumBytesRead += numBytesRead;
            float oldPercentage = this.percentage;
            this.percentage = (totalNumBytesRead / (float) maxNumBytes) * 100;
            propertyChangeSupport.firePropertyChange("totalNumBytesRead", oldTotalNumBytesRead, this.totalNumBytesRead);
            propertyChangeSupport.firePropertyChange("percentage", oldPercentage, this.percentage);
            logger.info("" + totalNumBytesRead + " / " + maxNumBytes + " - " + percentage + " %");
        }
        return numBytesRead;
    }
}
