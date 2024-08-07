package uk.ac.core.workers.item.pdf.conditions;

import java.io.File;
import java.util.concurrent.Callable;

public class FileSizeCondition implements Callable<Boolean> {
    final static long MAX_FILE_LENGTH = 2 * 1024 * 1024; // bytes, 2 MiB
    String filePath;

    FileSizeCondition(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public Boolean call() {
        System.out.println("Testing size" + filePath);

        File file = new File(filePath);
        return file.exists() && file.isFile() && file.length() < MAX_FILE_LENGTH;
    }
}
