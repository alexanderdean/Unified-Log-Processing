package hellocalculator.events;

import java.io.IOException;                                            // a
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;

public class PerformCalculationEvent extends Event {

  public final DirectObject directObject;

  private static final ObjectMapper MAPPER = JacksonUtils.newMapper(); // b
  private static final ValidationConfiguration cfg =
    ValidationConfiguration.newBuilder()
    .setDefaultVersion(SchemaVersion.DRAFTV4).freeze();
  private static final JsonValidator validator = JsonSchemaFactory.newBuilder()
    .setValidationConfiguration(cfg).freeze().getValidator();
  private static final JsonNode schema;
  static {
    try {
        schema = JsonLoader.fromResource("/raw_calculation_schema.json");
    }
    catch (IOException e) {
        throw new RuntimeException("Problem loading enriched schema from resource", e);
    }
  }  

  public PerformCalculationEvent() {
    this.directObject = null;
  };

  public PerformCalculationEvent(String operation,
    String[] args, Integer result) {
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

  public static Optional<PerformCalculationEvent> parse(String json) { // c

    Optional<PerformCalculationEvent> event;
    try {
      JsonNode node = MAPPER.readTree(json);
      ProcessingReport report = validator.validate(schema, node);
      System.out.println(report.toString());
      event = (report.isSuccess()) 
        ? event = Optional.of(MAPPER.readValue(json, PerformCalculationEvent.class))
        : Optional.empty();
    } catch (IOException | ProcessingException e) {
      System.out.println("oh no" + e);
      event = Optional.empty();
    }
    return event;
  }

}
