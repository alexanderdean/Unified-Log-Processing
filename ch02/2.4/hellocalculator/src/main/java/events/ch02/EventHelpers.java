package events.ch02;

import java.util.Properties;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class EventHelpers {

  static Producer<String, String> createProducer(String brokerList) {
    Properties props = new Properties();
    props.put("metadata.broker.list", brokerList);
    props.put("serializer.class", "kafka.serializer.StringEncoder");
    props.put("request.required.acks", "1");
    ProducerConfig config = new ProducerConfig(props);
    return new Producer<String, String>(config);
  }

  static void trackEvent(Producer<String, String> producer, String key, String message) {
    KeyedMessage<String, String> data = new KeyedMessage<String, String>("calc_events", key, message);
    producer.send(data);
  }
}
