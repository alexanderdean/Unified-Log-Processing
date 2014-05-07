package hellocalculator;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import com.github.fge.jsonschema.main.JsonValidator;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.junit.Test;

public class EventSchemaTest {

  private final JsonValidator validator;
  private final JsonNode eventSchema;

  public EventSchemaTest() {

    final ValidationConfiguration cfg = ValidationConfiguration.newBuilder()
      .setDefaultVersion(SchemaVersion.DRAFTV4).freeze();
  
    validator = JsonSchemaFactory.newBuilder()
      .setValidationConfiguration(cfg).freeze().getValidator();

    eventSchema = JsonLoader.fromResource("/event_schema.json");
  }

  @Test
  public void testPerformCalculationEvent() throws ProcessingException {
    PerformCalculationEvent calculation = new PerformCalculationEvent(
      "addition", new String[] { "23", "17" }, "40");
    final ProcessingReport report = validator.validate(eventSchema, calculation.asJSON());
    assertTrue(report.isSuccess());
  }

  @Test
  public void testInputBadDataEvent() throws ProcessingException {
    InputBadDataEvent badData = new InputBadDataEvent(
      new String[] { "23", "17", "ohno" }, "not all inputs parseable to Integers");
    final ProcessingReport report = validator.validate(eventSchema, badData.asJSON());
    assertTrue(report.isSuccess());
  } 
}