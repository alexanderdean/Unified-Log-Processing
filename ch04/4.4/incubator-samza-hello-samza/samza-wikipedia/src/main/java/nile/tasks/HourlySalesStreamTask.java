package nile.tasks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.codehaus.jackson.type.TypeReference;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;

public class HourlySalesStreamTask
  implements StreamTask, InitableTask, WindowableTask {

  private static final DateTimeFormatter EVENT_DTF =
    DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
  private static final DateTimeFormatter KEY_DTF =
    DateTimeFormat.forPattern("yyyy-MM-dd HH:'00':'00'");

  private KeyValueStore<String, Integer> store;
  private Set<String> hours = new HashSet<String>();                     // a

  public void init(Config config, TaskContext context) {                 // b
    this.store = (KeyValueStore<String, Integer>)
      context.getStore("nile-hourlysales");
  }

  @SuppressWarnings("unchecked")
  @Override
  public void process(IncomingMessageEnvelope envelope,
    MessageCollector collector, TaskCoordinator coordinator) {           // c

    Map<String, Object> event = (Map<String, Object>) envelope.getMessage();
    String verb = (String) event.get("verb");
    
    if (verb.equals("place")) {

      String timestamp = (String) ((Map<String, Object>)
        event.get("context")).get("timestamp");
      String hourKey = asHourKey(timestamp);

      Integer hourlySales = store.get(hourKey);
      if (hourlySales == null) hourlySales = 0;
      Double orderValue = (Double) ((Map<String, Object>) ((Map<String, Object>)
        event.get("directObject")).get("order")).get("value") * 100.0;   // d

      store.put(hourKey, hourlySales + orderValue.intValue());
      hours.add(hourKey);
    }
  }

  @Override
  public void window(MessageCollector collector,
    TaskCoordinator coordinator) {                                       // e

    Map<String, Double> sales = new HashMap<String, Double>();
    for (String hour : hours) {
      Double hourlySales = store.get(hour) / 100.0;                      // f
      sales.put(hour, hourlySales);
    }
    collector.send(new OutgoingMessageEnvelope(
      new SystemStream("kafka", "nile-hourlysales-stats"), sales));

    hours.clear();                                                       // g
  }

  private static String asHourKey(String timestamp) {                    // h
    try {
      return KEY_DTF.print(EVENT_DTF.parseDateTime(timestamp));
    } catch (IllegalArgumentException iae) {
      return "unknown";
    }
  }
}
