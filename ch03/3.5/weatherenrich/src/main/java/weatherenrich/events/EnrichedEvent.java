package weatherenrich.events;

import java.util.List;

public class EnrichedEvent extends RawEvent {

  public final EnrichedContext context;

  public EnrichedEvent(RawEvent rawEvent,
    double temperature, List<String> conditions) {                     // a

    this.subject = rawEvent.subject;
    this.verb = rawEvent.verb;
    this.directObject = rawEvent.directObject;
    this.context = new EnrichedContext(
      rawEvent.context.timestamp, temperature, conditions);
  }

  public static final class EnrichedContext extends Context {          // b
    public final String timestamp;
    public final Weather weather;

    public EnrichedContext(String timestamp,
      double temperature, List<String> conditions) {

      this.timestamp = timestamp;
      this.weather = new Weather(temperature, conditions);
    }

    public static final class Weather {
      public final double temperature;
      public final List<String> conditions;

      public Weather(double temperature, List<String> conditions) {
        this.temperature = temperature;
        this.conditions = conditions;
      }
    }
  }
}
