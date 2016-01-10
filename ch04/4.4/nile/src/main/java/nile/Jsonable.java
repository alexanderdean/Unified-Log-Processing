package nile;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

public interface Jsonable {

  static final ObjectMapper MAPPER = new ObjectMapper()
    .registerModule(new Jdk8Module())
    .registerModule(new JSR310Module());

  public default String asJson() {
    try {
      return MAPPER.writeValueAsString(this);
    } catch (IOException ioe) {
      throw new RuntimeException("Problem writing class as JSON", ioe);
    }    
  }

  public static <T extends Jsonable> T fromJson(String json, Class<T> clazz) {
    try {
      MAPPER.readValue(json, clazz);
    } catch (IOException ioe) {
      throw new RuntimeException("Problem creating class from JSON", ioe);
    }
  }
}
