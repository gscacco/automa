package org.gsc.test.utils;
import org.gsc.automa.AutomaEvent;
import org.gsc.automa.AutomaFactory;
import org.gsc.automa.AutomaState;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

public class AutomaTestCase extends Assert {
  
  private long _seed;
  private AutomaFactory _af;

  @Before
  public void before() {
    _seed = 0;
    _af  = new AutomaFactory();
  }

  @After
  public void after() {
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
