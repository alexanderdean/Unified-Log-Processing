package hellocalculator;

import java.io.IOException;

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

import hellocalculator.events.PerformCalculationEvent;
import hellocalculator.events.InputBadDataEvent;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class EventSchemaTest {

  private static final ObjectMapper MAPPER = JacksonUtils.newMapper();

  private final JsonValidator validator;
  private final JsonNode eventSchema;

  public EventSchemaTest() throws IOException {

    final ValidationConfiguration cfg =
      ValidationConfiguration.newBuilder()
      .setDefaultVersion(SchemaVersion.DRAFTV4).freeze();
  
    validator = JsonSchemaFactory.newBuilder()
      .setValidationConfiguration(cfg).freeze().getValidator();

    eventSchema = JsonLoader.fromResource("/event_schema.json");
  }

  @Test
  public void validatePerformCalculationEvent()
    throws ProcessingException, IOException {

    PerformCalculationEvent calculation = new PerformCalculationEvent(
      "addition", new String[] { "23", "17" }, 40);
    JsonNode node = MAPPER.readTree(calculation.asJson());
    final ProcessingReport report = validator.validate(eventSchema, node);
    assertTrue(report.isSuccess());
  }

  @Test
  public void validateInputBadDataEvent()
    throws ProcessingException, IOException {

    InputBadDataEvent badData = new InputBadDataEvent(
      new String[] { "23", "17", "ohno" },
      "not all inputs parseable to Integers");
    JsonNode node = MAPPER.readTree(badData.asJson());
    final ProcessingReport report = validator.validate(eventSchema, node);
    assertTrue(report.isSuccess());
  } 
}
