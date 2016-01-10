package nile;

import java.io.IOException;

public interface IJsonable {

  protected static final ObjectMapper MAPPER = new ObjectMapper()
    .registerModule(new Jdk8Module())
    .registerModule(new JavaTimeModule());

  public String asJson() {
    try {
      return MAPPER.writeValueAsString(this);
    } catch (IOException ioe) {
      throw new RuntimeException("Problem writing class as JSON", ioe);
    }
  }

  public IJsonable fromJson(String json);
}
