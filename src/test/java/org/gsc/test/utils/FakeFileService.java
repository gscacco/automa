package org.gsc.test.utils;

import org.gsc.automa.config.IOutputStreamService;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 03/07/13
 * Time: 16.59
 * To change this template use File | Settings | File Templates.
 */
public class FakeFileService implements IOutputStreamService {
    private boolean exception = false;

    @Override
    public void write(String str) throws IOException {
        if (exception) {
            throw new IOException("Fake exception");
        }
    }

    public void setThrowExceptionOnWrite() {
        this.exception = true;
    }
}
