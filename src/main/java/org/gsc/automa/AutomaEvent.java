package org.gsc.automa;

/**
 * Created with IntelliJ IDEA.
 * User: gianluca
 * Date: 04/04/13
 * Time: 16.29
 * To change this template use File | Settings | File Templates.
 */
public class AutomaEvent {
    private Object currentObject;
    private String name;

    public AutomaEvent(String name) {
        this.name = name;
    }

    public void setCurrentObject(Object currentObject) {
        this.currentObject = currentObject;
    }

    public Object getCurrentObject() {
        return currentObject;
    }

    @Override
    public String toString() {
        return name + "[" + name + "]";
    }
}
