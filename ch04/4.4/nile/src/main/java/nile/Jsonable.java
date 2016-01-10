package nile;

import java.io.IOException;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

public interface Jsonable {

  static final ObjectMapper MAPPER = new ObjectMapper()
    .registerModule(new Jdk8Module())
    .registerModule(new JSR310Module())
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);         // a   

  public static String asJson(Object o) {                             // b
    try {
      return MAPPER.writeValueAsString(o);
    } catch (IOException ioe) {
      throw new RuntimeException("Problem writing class as JSON", ioe);
    }  
  }

  public default String asJson() {                                    // c
    return asJson(this);
  }

  public static <T extends Object> T fromJson(String json,
  Class<T> clazz) {                                                   // d
    try {
      return MAPPER.readValue(json, clazz);
    } catch (IOException ioe) {
      throw new RuntimeException("Problem creating class from JSON", ioe);
    }
  }
}
