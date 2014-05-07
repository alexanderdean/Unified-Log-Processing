package events.ch02;

import java.util.Arrays;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class HelloCalculatorTest {
    @Test
    public void testSum() {
        assertThat(HelloCalculator.sum(Arrays.asList("23", "17", "5")), equalTo(45));
    }
}
