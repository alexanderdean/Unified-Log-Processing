package hellocalculator;

import 

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class EventSchemaTest {
  @Test
  public void testPerformCalculationEvent() {
    assertThat(HelloCalculator.sum(new String[] { "23", "17", "5" }), equalTo(45));
  }

  @Test
  public void testInputBadDataEvent() {
    assertThat(HelloCalculator.sum(new String[] { "23", "17", "5" }), equalTo(45));
  } 
}