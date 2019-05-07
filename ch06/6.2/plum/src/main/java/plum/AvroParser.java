package plum;

import java.io.*;
import java.util.*;
import java.util.Base64.Encoder;

import org.apache.avro.*;
import org.apache.avro.io.*;
import org.apache.avro.generic.GenericData;
import org.apache.avro.specific.*;

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

  private static Encoder base64 = Base64.getEncoder();

  public static Optional<Check> fromJsonAvro(String event) {

    InputStream is = new ByteArrayInputStream(event.getBytes());
    DataInputStream din = new DataInputStream(is);

    try {
      Decoder decoder = DecoderFactory.get().jsonDecoder(schema, din);
      DatumReader<Check> reader = new SpecificDatumReader<Check>(schema);
      return Optional.of(reader.read(null, decoder));                  // c
    } catch (IOException | AvroTypeException e) {
      System.out.println("Error deserializing:" + e.getMessage());
      return Optional.empty();
    }
  }

  public static Optional<String> toBase64(Check check) {

    ByteArrayOutputStream bout = new ByteArrayOutputStream();

    DatumWriter<Check> writer = new SpecificDatumWriter<Check>(schema);
    BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(bout, null);
    try {
      writer.write(check, encoder);
      encoder.flush();
      return Optional.of(base64.encodeToString(bout.toByteArray()));   // d
    } catch (IOException e) {
      System.out.println("Error serializing:" + e.getMessage());
      return Optional.empty();
    }
  }
}
