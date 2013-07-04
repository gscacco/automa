package org.gsc.automa.config;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 03/07/13
 * Time: 17.00
 * To change this template use File | Settings | File Templates.
 */
public interface IOutputStreamService {
    void write(String str) throws IOException;
}
