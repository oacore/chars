package uk.ac.core.common.util;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

/**
 * @author iknoth
 */
public class InputOutput {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(InputOutput.class);

    public InputOutput() {
    }

    public List<List<String>> readCSVFile(String filename, String separator) {

        List<String> lines = this.readFile(filename);
        List<List<String>> csvItems = new LinkedList<List<String>>();

        for (String line : lines) {
            List<String> items = new LinkedList<String>();
            String[] it = line.split(separator);
            for (String i : it) {
                i = i.trim();
                i = i.replaceAll("\"", " ");
                items.add(i);
            }

            csvItems.add(items);

        }

        return csvItems;

    }

    public String readFileUTF8(String filename) {

        File f = new File(filename);
        if (!f.exists()) {
            return null;
        }

        List<String> fileArr = this.readFileToListUTF8(filename);
        StringBuilder sb = new StringBuilder();
        for (String str : fileArr) {
            sb.append(str);
            sb.append("\n");
        }

        return sb.toString();
    }

    public ArrayList<String> readFileToListUTF8(String filename) {

        ArrayList<String> fileText = new ArrayList<String>();
        try {
            FileInputStream fstream = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF8"));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                fileText.add(strLine.trim());
            }
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return fileText;
    }

    public ArrayList<String> readFile(String filename) {

        ArrayList<String> fileText = new ArrayList<String>();
        try {
            FileInputStream fstream = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                fileText.add(strLine.trim());
            }
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return fileText;
    }

    public String readFileText(String f) {

        ArrayList<String> fileArr = this.readFile(f);
        StringBuilder sb = new StringBuilder();
        for (String str : fileArr) {
            sb.append(str);
            sb.append("\n");
        }

        return sb.toString();
    }

    public byte[] readFileContent(String filePath) throws FileNotFoundException, IOException {
        FileInputStream in = null;
        try {
            int max = (int) new File(filePath).length();
            int c = 0;
            int offset = 0;
            byte fileContent[] = new byte[max];
            in = new FileInputStream(filePath);

            while ((in != null) && max > 0 && ((c = in.read(fileContent, offset, max)) != -1)) {
                max -= c;
                offset += c;
            }

            return fileContent;

        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                logger.error(ex.getMessage());
            }
        }
    }

    //    public String readFileTextAsBytes(String filename) {
//        BufferedInputStream fis = null;
//        File f = new File(filename);
//        String text = null;
//        try {
//            fis = new BufferedInputStream(new FileInputStream(filename));
//            byte[] b = new byte[f.length()];
//            int read = 0;
//            int offset = 0;
//            while ((read = (fis.read(b, 0, b.length))) > 0) {
//                offset += read;
//            }
//
//            text = new String(b, "UTF-8");
//
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(InputOutput.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//                    Logger.getLogger(InputOutput.class.getName()).log(Level.SEVERE, null, ex);
//        } finally {
//            try {
//                fis.close();
//            } catch (IOException ex) {
//                Logger.getLogger(InputOutput.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//
//        return text;
//    }
    public HashSet<String> readFileToHashSet(String filename) {

        HashSet<String> fileText = new HashSet<String>();
        try {
            FileInputStream fstream = new FileInputStream(filename);
            // Get the object of DataInputStream
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            //Read File Line By Line
            while ((strLine = br.readLine()) != null) {
                // Print the content on the console
                fileText.add(strLine.trim().toLowerCase());
            }
            //Close the input stream
            in.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return fileText;
    }

    public HashSet<String> readFilesToHashSet(ArrayList<String> filenames) {

        HashSet<String> fileText = new HashSet<String>();
        try {
            for (String filename : filenames) {
                FileInputStream fstream = new FileInputStream(filename);
                // Get the object of DataInputStream
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String strLine;
                //Read File Line By Line
                while ((strLine = br.readLine()) != null) {
                    // Print the content on the console
                    fileText.add(strLine.trim());
                }
                //Close the input stream
                in.close();
            }
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
        return fileText;
    }

    public void writeToFile(String filename, ArrayList<Double> results) {

        try {
            // Create file 
            FileWriter fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);

            for (Double line : results) {

                out.write(line.toString() + "\n");
            }

            //Close the output stream
            out.close();
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        }
    }

    public void writeStringToFile(String filename, String text) {

        BufferedWriter out = null;
        try {
            // Create file 
            FileWriter fstream = new FileWriter(filename);
            out = new BufferedWriter(fstream);

            out.write(text);
        } catch (Exception e) {//Catch exception if any
            System.err.println("Error: " + e.getMessage());
        } finally {
            if (out != null) {
                try {
                    //Close the output stream
                    out.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public void writeDataToFile(File file, byte[] data) {
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(file);
            IOUtils.write(data, output);
        } catch (FileNotFoundException ex) {
            logger.warn("File not found " + file.getName() + " " + ex, this.getClass());
        } catch (IOException ex) {
            logger.warn("Cannot write to file " + file.getName() + " " + ex, this.getClass());
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    public void writeUTF8StringToFile(String filename, String text) {

        File file = new File(filename);

        try {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF8"));
            out.write(text);
            out.close();
        } catch (IOException e) {
        }

    }

    public void addToFile(String filename, String text) {

        BufferedWriter bw = null;

        try {

            if (!new File(filename).exists()) {
                this.writeStringToFile(filename, text);
                return;
            }

            bw = new BufferedWriter(new FileWriter(filename, true));
            bw.write(text);
            bw.flush();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {                       // always close the file
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException ioe2) {
                }
            }
        } // end try/catch/finally
    }

    /**
     * Get file content.
     *
     * @return
     */
    public String getFileContent(File file) {
        FileInputStream stream = null;
        String content = null;
        try {
            stream = new FileInputStream(file);
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb;
            bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            content = Charset.defaultCharset().decode(bb).toString();
        } catch (FileNotFoundException ex) {
            logger.debug("Cannot open file input stream: " + ex, this.getClass());
        } catch (IOException ex) {
            logger.debug("Cannot create mapped byte buffer: " + ex, this.getClass());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException ex1) {
                }
            }
        }
        return content;
    }

    /**
     * Get url content.
     *
     * @return
     */
    public String getUrlContent(URL url) {
        URLConnection connection = null;
        InputStream is = null;
        BufferedReader in = null;
        try {
            connection = url.openConnection();
            connection.connect();
            is = connection.getInputStream();
            // reading of content
            in = new BufferedReader(new InputStreamReader(is));
            StringBuilder content = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            return content.toString();
        } catch (IOException ex) {
            logger.warn("Error for url " + url.toString() + " : " + ex, this.getClass());
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
            if (connection != null) {
                HttpURLConnection httpConnection = (HttpURLConnection) connection;
                httpConnection.disconnect();
            }
        }
    }


    public void writeObjectToJsonFile(Object object, String path) {
        String jsonObject = new Gson().toJson(object);
        this.writeStringToFile(path, jsonObject);
    }

    public Object readFileToJsonObject(String path, Type classType) {
        String content = this.readFileText(path);
        return new Gson().fromJson(content, classType);
    }


    public static void main(String[] args) {

        InputOutput io = new InputOutput();
        long milis = System.currentTimeMillis();
        //io.writeStringToFile("test.txt", io.readFileTextAsBytes("/Volumes/core-data/pdf_stores/text/oro.open.ac.uk/1348.txt"));
        io.writeStringToFile("test.txt", io.readFileText("/Volumes/core-data/pdf_stores/text/oro.open.ac.uk/1348.txt"));
        long milis2 = System.currentTimeMillis();
        System.out.println(milis2 - milis);

    }
}

