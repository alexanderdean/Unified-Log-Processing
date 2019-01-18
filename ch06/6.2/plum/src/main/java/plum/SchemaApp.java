package plum;

import java.util.*;

import plum.avro.Check;

public class SchemaApp {

  public static void main(String[] args){
    String event = args[0];

    Optional<Check> maybeCheck = AvroParser.fromJsonAvro(event);       // a

    maybeCheck.ifPresent(check -> {
      System.out.println("Deserialized check event:");
      System.out.println(check);

      Optional<String> maybeBase64 = AvroParser.toBase64(check);
      maybeBase64.ifPresent(base64 -> {
        System.out.println("Re-serialized check event in Base64:");
        System.out.println(base64);
      });
    });

  }
}
