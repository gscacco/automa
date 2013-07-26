package org.gsc.test.utils;
import org.gsc.automa.AutomaEvent;
import org.gsc.automa.AutomaFactory;
import org.gsc.automa.AutomaState;
import org.junit.Assert;

public class AutomaTestCase extends Assert {
  
  private long _seed;
  private AutomaFactory _af;

  protected void setUp() {
    _seed = 0;
    _af  = new AutomaFactory();
  }

  protected void tearDown() {
  }

  public AutomaState nextState() {
    return _af.createState(nextString("state"));
  }

  public AutomaEvent nextEvent() {
    return new AutomaEvent(nextString("event"));
  }

  public String nextString(String base) {
    return base + "_" + Long.toString(_seed++);
  }

}
