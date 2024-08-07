package uk.ac.core.metadatadownloadworker.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrossrefFileOutputStream extends FileOutputStream {

    public CrossrefFileOutputStream(String name) throws FileNotFoundException {
        super(name);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        String line = new String(bytes, StandardCharsets.UTF_8);
        if (line.contains("CDATA")) {
            Map<String, String> toReplace = new HashMap<>();

            Pattern pattern = Pattern.compile("(?:<!\\[CDATA\\[)([^>]+)(?:\\]>)");
            Matcher matcher = pattern.matcher(line);
            while (matcher.find()) {
                String cdata = matcher.group();

                if (cdata.contains("[[") || cdata.contains("]]]")) {
                    String replaced = cdata;
                    if (cdata.contains("[[")) {
                        replaced = replaced.replace("[[", "[");
                    }
                    if (cdata.contains("]]]")) {
                        replaced = replaced.replace("]]]", "]]");
                    }
                    toReplace.put(cdata, replaced);

                } else if (!cdata.contains("]]")) {
                    toReplace.put(cdata, cdata.replace("]", "]]"));
                }
            }

            String replacedString = line;
            if (!toReplace.isEmpty()) {
                for (Map.Entry<String, String> entry : toReplace.entrySet()) {
                    replacedString = replacedString.replace(entry.getKey(), entry.getValue());
                }
            }

            super.write(replacedString.getBytes(StandardCharsets.UTF_8));
        } else {
            super.write(bytes);
        }
    }
}
