package org.gsc.automa;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 04/04/13
 * Time: 16.28
 * To change this template use File | Settings | File Templates.
 */
public class AutomaFactory {
    public AutomaFactory() {
    }

    public AutomaState createState(String name) {
        return new AutomaState(name);
    }
}
