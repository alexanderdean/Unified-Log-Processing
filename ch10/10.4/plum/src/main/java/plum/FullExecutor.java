package plum;

import java.io.*;

import org.apache.kafka.clients.producer.*;

import org.apache.avro.*;
import org.apache.avro.io.*;
import org.apache.avro.generic.GenericData;
import org.apache.avro.specific.SpecificDatumReader;

import plum.avro.Alert;

public class FullExecutor implements IExecutor {

  private final KafkaProducer<String, String> producer;
  private final String eventsTopic;

  private static Schema schema;
  static {
    try {
      schema = new Schema.Parser()
        .parse(EchoExecutor.class.getResourceAsStream("/avro/alert.avsc"));
    } catch (IOException ioe) {
      throw new ExceptionInInitializerError(ioe);
    }
  }

  public FullExecutor(String servers, String eventsTopic) {

    this.producer = new KafkaProducer(
      IExecutor.createConfig(servers));
    this.eventsTopic = eventsTopic;
  }

  public void execute(String command) {

    InputStream is = new ByteArrayInputStream(command.getBytes());
    DataInputStream din = new DataInputStream(is);

    try {
      Decoder decoder = DecoderFactory.get().jsonDecoder(schema, din);
      DatumReader<Alert> reader = new SpecificDatumReader<Alert>(schema);
      Alert alert = reader.read(null, decoder);
      Emailer.send(alert);                                            // a
      IExecutor.write(this.producer, this.eventsTopic,
        "{ \"event\": \"email_sent\" }");                             // b
    } catch (IOException | AvroTypeException e) {
      System.out.println("Error executing command:" + e.getMessage());
    }
  }
}
