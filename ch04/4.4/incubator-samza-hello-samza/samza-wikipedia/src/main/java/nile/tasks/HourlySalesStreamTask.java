/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package nile.tasks;                                                      // a

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class HourlySalesStreamTask
  implements StreamTask, InitableTask, WindowableTask {

  private static final DateTimeFormatter EVENT_DTF =
    DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");
  private static final DateTimeFormatter KEY_DTF =
    DateTimeFormat.forPattern("yyyy-MM-dd HH:'00':'00'");

  private KeyValueStore<String, Integer> store;
  private Set<String> hours = new HashSet<String>();                     // b

  public void init(Config config, TaskContext context) {                 // c
    this.store = (KeyValueStore<String, Integer>)
      context.getStore("nile-hourlysales");
  }

  @SuppressWarnings("unchecked")
  @Override
  public void process(IncomingMessageEnvelope envelope,
    MessageCollector collector, TaskCoordinator coordinator) {           // d

    Map<String, Object> event = (Map<String, Object>) envelope.getMessage();
    String verb = (String) event.get("verb");
    
    if (verb.equals("place")) {

      String timestamp = (String) ((Map<String, Object>)
        event.get("context")).get("timestamp");
      String hourKey = asHourKey(timestamp);

      Integer hourlySales = store.get(hourKey);
      if (hourlySales == null) hourlySales = 0;
      Double orderValue = (Double) ((Map<String, Object>)
        event.get("directObject")).get("orderValue") * 100.0;            // e

      store.put(hourKey, hourlySales + orderValue.intValue());
      hours.add(hourKey);
    }
  }

  @Override
  public void window(MessageCollector collector,
    TaskCoordinator coordinator) {                                       // f

    Map<String, Double> sales = new HashMap<String, Double>();
    for (String hour : hours) {
      Double hourlySales = store.get(hour) / 100.0;                      // e
      sales.put(hour, hourlySales);
    }
    collector.send(new OutgoingMessageEnvelope(
      new SystemStream("kafka", "hourlysales_stats"), sales));

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
