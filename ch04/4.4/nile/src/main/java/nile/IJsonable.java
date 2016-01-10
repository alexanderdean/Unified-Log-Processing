package nile;

import java.io.IOException;

public interface IJsonable {

  protected static final ObjectMapper MAPPER = new ObjectMapper()
    .registerModule(new Jdk8Module())
    .registerModule(new JavaTimeModule());

  public static String asJson(Object o) {
    try {
      return MAPPER.writeValueAsString(this);
    } catch (IOException ioe) {
      throw new RuntimeException("Problem writing class as JSON", ioe);
    }    
  }

  public static Object fromJson(String json, JavaType jt) {
    try {
      MAPPER.readValue(json, jt);
    } catch (IOException | ProcessingException e) {
      throw new RuntimeException("Problem creating class from JSON", ioe);
    }
  }

  public String asJson() {
    asJson(this);
  }

  public static IJsonable fromJson(String json);
}
