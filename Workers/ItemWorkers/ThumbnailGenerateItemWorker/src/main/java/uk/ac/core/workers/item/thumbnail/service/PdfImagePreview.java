package uk.ac.core.workers.item.thumbnail.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generates image preview from PDF.
 *
 * @author pk3295?, mk6353
 */
public class PdfImagePreview {

    private static final Logger logger = LoggerFactory.getLogger(PdfImagePreview.class);

    private String scriptLocation;

    public PdfImagePreview() {
        if (new File("/data/core/scripts/montage.sh").exists()) {
            this.scriptLocation = "/data/core/scripts/montage.sh";
        } else if (new File("/data/remote/core/scripts/montage.sh").exists()) {
            this.scriptLocation = "/data/core/remote/scripts/montage.sh";
        }
        logger.info("Script Location: {}", this.scriptLocation);
    }

    /**
     * Generates image preview from PDF.
     *
     * @param src Path of the input PDF file.
     * @param dst Path of the output JPG file.
     * @param size Size of the output image in px.
     * @return
     */
    public boolean generateImage(String src, String dst, String size) {
        logger.info("Generating Image source: {} dest: {} size: {}", src, dst, size);

        String[] cmd = new String[4];
        cmd[0] = scriptLocation;
        cmd[1] = src;
        cmd[2] = dst;
        cmd[3] = size;

        ProcessBuilder pb = new ProcessBuilder(cmd);
        try {
            Process p = pb.start();
            long now = System.currentTimeMillis();

            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }
            String result = builder.toString();
            System.out.println(result);
            
            // If process does not complete within 10 seconds, destroy and continue
            long timeoutInMillis = 1000L * 10;
            long finish = now + timeoutInMillis;
            while (isAlive(p)) {
                Thread.sleep(100);
                if (System.currentTimeMillis() > finish) {
                    logger.error("Task failed to complete within 10 seconds Source:" + src);
                    p.destroy();
                }

            }

        } catch (IOException | InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
        }

        return true;

    }

    public static boolean isAlive(Process p) {
        try {
            p.exitValue();
            return false;
        } catch (IllegalThreadStateException e) {
            return true;
        }
    }

    public static void main(String[] args) {
        String pdfName = "/data/core/33992680.pdf";
        String imageName = "/data/core/33992680.jpg";
        new PdfImagePreview().generateImage(pdfName, imageName, "75");
    }
}
