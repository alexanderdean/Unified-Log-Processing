package weatherenrich.events;                                          // a

import java.io.IOException;                                            // b
import java.util.Optional;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;

public class RawEvent extends Event {                                  // c

  public DirectObject directObject;

  private static final ValidationConfiguration cfg =                   // d
    ValidationConfiguration.newBuilder()
    .setDefaultVersion(SchemaVersion.DRAFTV4).freeze();
  private static final JsonValidator validator =
    JsonSchemaFactory.newBuilder()
    .setValidationConfiguration(cfg).freeze().getValidator();
  private static final JsonNode schema;
  static {
    try {
      schema = JsonLoader.fromResource("/raw_event_schema.json");
    }
    catch (IOException e) {
      throw new RuntimeException("Problem loading raw event's schema", e);
    }
  }  

  public RawEvent() {                                                  // e
    this.directObject = null;
  }

  public RawEvent(String operation,
    String[] args, Integer result) {                                   // f
    super("perform");
    this.directObject = new DirectObject(operation, args, result);
  }

  public static class DirectObject {
    public final Calculation calculation;

    public DirectObject() {
      this.calculation = null;
    }

    public DirectObject(String operation, String[] args, Integer result) {
      this.calculation = new Calculation(operation, args, result);
    }

    public static class Calculation {
      public final String operation;
      public final String[] args;
      public final Integer result;

      public Calculation() {
        this.operation = null;
        this.args = null;
        this.result = null;
      }

      public Calculation(String operation, String[] args, Integer result) {
        this.operation = operation;
        this.args = args;
        this.result = result;
      }
    }
  }

  public static Optional<RawEvent> parse(String json) {                // g

    Optional<RawEvent> event;
    try {
      JsonNode node = MAPPER.readTree(json);
      ProcessingReport report = validator.validate(schema, node);
      event = (report.isSuccess()) 
        ? Optional.of(MAPPER.readValue(json, RawEvent.class))
        : Optional.empty();
    } catch (IOException | ProcessingException e) {
      event = Optional.empty();
    }
    return event;
  }
}
