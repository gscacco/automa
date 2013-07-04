package org.gsc.automa.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 03/07/13
 * Time: 17.05
 * To change this template use File | Settings | File Templates.
 */
public class FileOutputStreamService implements IOutputStreamService {
    private FileOutputStream fileOutputStream = null;

    public FileOutputStreamService() {
        try {
            fileOutputStream = new FileOutputStream(File.createTempFile("AutomaSequenceDiagram", ".txt"));
        } catch (IOException e) {
        }
    }

    @Override
    public void write(String str) throws IOException {
        if (fileOutputStream == null) {
            throw new IOException("The file was not created");
        }
        this.fileOutputStream.write(str.getBytes());
    }
}
