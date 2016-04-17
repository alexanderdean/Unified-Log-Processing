package plum;

import java.util.Properties;
import java.io.*;

import org.apache.kafka.clients.producer.*;

import org.apache.avro.*;
import org.apache.avro.io.*;
import org.apache.avro.generic.GenericData;
import org.apache.avro.specific.SpecificDatumReader;

import plum.avro.*;

public class EchoExecutor implements IExecutor {

  private final KafkaProducer<String, String> producer;
  private final String eventsTopic;
  private final Properties properties;

  private static Schema schema;
  static {
    try {
      schema = new Schema.Parser()
        .parse(EchoExecutor.class.getResourceAsStream("/avro/alert.avsc"));
      //GenericData.setStringType(schema, GenericData.StringType.String);
    } catch (IOException ioe) {
      throw new ExceptionInInitializerError(ioe);
    }
  }

  public EchoExecutor(String servers, String eventsTopic,
    Properties properties) {

    this.producer = new KafkaProducer(
      IExecutor.createConfig(servers));
    this.eventsTopic = eventsTopic;
    this.properties = properties;
  }

  public void execute(String command) {

    InputStream is = new ByteArrayInputStream(command.getBytes());
    DataInputStream din = new DataInputStream(is);

    try {
      Decoder decoder = DecoderFactory.get().jsonDecoder(schema, din);
      DatumReader<Alert> reader = new SpecificDatumReader<Alert>(schema);
      Alert alert = reader.read(null, decoder);
      System.out.println("Alert " + alert.recipient.name + " about " +
        alert.notification.summary);
    } catch (IOException | AvroTypeException e) {
      System.out.println("Error executing command:" + e.getMessage());
    }  

    // IExecutor.write(this.producer, this.eventsTopic, message);
  }
}
