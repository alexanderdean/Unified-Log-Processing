package plum;

import java.io.*;
import java.util.Optional;

import org.apache.avro.*;
import org.apache.avro.io.*;
import org.apache.avro.generic.GenericData;
import org.apache.avro.specific.SpecificDatumReader;

import plum.avro.Check;                                                // a

public class AvroParser {

  private static Schema schema;
  static {
    try {                                                              // b
      schema = new Schema.Parser()
        .parse(AvroParser.class.getResourceAsStream("/avro/check.avsc"));
    } catch (IOException ioe) {
      throw new ExceptionInInitializerError(ioe);
    }
  }

  public static Optional<Check> fromJsonAvro(String event) {

    InputStream is = new ByteArrayInputStream(event.getBytes());
    DataInputStream din = new DataInputStream(is);

    Check check = null;
    try {
      Decoder decoder = DecoderFactory.get().jsonDecoder(schema, din);
      DatumReader<Check> reader = new SpecificDatumReader<Check>(schema);
      return Optional.of(reader.read(null, decoder));                  // c
    } catch (IOException | AvroTypeException e) {
      System.out.println("Error executing command:" + e.getMessage());
      return Optional.empty();
    }
  }
}

