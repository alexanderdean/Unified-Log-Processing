package hellocalculator;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class HelloCalculatorTest {
  @Test
  public void testSum() {
      assertThat(HelloCalculator.sum("23", "17", "5"), equalTo(45));
  }
}
