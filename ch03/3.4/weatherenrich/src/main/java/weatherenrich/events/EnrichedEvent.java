package weatherenrich.events;

public class EnrichedEvent extends RawEvent {

  public final EnrichedContext context;

  public EnrichedEvent(RawEvent rawEvent,
    Float temperature, String[] conditions) {

    this.subject = rawEvent.subject;
    this.verb = rawEvent.verb;
    this.directObject = rawEvent.directObject;
    this.context = new EnrichedContext(
      rawEvent.context.timestamp, temperature, conditions);
  }

	public static final class EnrichedContext extends Context {
    public final String timestamp;
    public final Weather weather;

    public EnrichedContext(String timestamp,
      Float temperature, String[] conditions) {

      this.timestamp = timestamp;
      this.weather = new Weather(temperature, conditions);
    }

    public static final class Weather {
      public final Float temperature; // TODO: check precision
      public final String[] conditions; // TODO: Array or List?

      public Weather(Float temperature, String[] conditions) {
        this.temperature = temperature;
        this.conditions = conditions;
      }
    }
  }
}
