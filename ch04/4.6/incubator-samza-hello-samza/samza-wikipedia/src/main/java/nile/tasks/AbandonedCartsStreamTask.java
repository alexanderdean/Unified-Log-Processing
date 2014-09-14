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

import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.storage.kv.KeyValueIterator;
import org.apache.samza.storage.kv.Entry;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.StreamTask;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.apache.samza.task.WindowableTask;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

public class AbandonedCartsStreamTask
  implements StreamTask, InitableTask, WindowableTask {

  private static final int ABANDONED_AFTER_SECS = 2700;
  private static final DateTimeFormatter EVENT_DTF =
    DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss").withZone(DateTimeZone.UTC);
  protected static final ObjectMapper MAPPER = new ObjectMapper();

  private KeyValueStore<String, String> store;

  public void init(Config config, TaskContext context) {
    this.store = (KeyValueStore<String, String>)
      context.getStore("nile-abandonedcarts");
  }

  @SuppressWarnings("unchecked")
  @Override
  public void process(IncomingMessageEnvelope envelope,
    MessageCollector collector, TaskCoordinator coordinator) {

    Map<String, Object> event = (Map<String, Object>) envelope.getMessage();
    String verb = (String) event.get("verb");
    String cookieId = (String) ((Map<String, Object>)
      event.get("subject")).get("cookieId");
    
    if (verb.equals("add")) {                                            // b
      String timestamp = (String) ((Map<String, Object>)
        event.get("context")).get("timestamp");

      String oldCart = store.get(asCartKey(cookieId));
      Map<String, Object> item = (Map<String, Object>)
        event.get("directObject");
      String updatedCart = updateCart(oldCart, item);

      store.put(asTimestampKey(cookieId), timestamp);
      store.put(asCartKey(cookieId), updatedCart);
    
    } else if (verb.equals("place")) {                                   // c
      resetShopper(cookieId);
    }
  }

  @Override
  public void window(MessageCollector collector,
    TaskCoordinator coordinator) {

    KeyValueIterator<String, String> entries = store.all();
    while (entries.hasNext()) {                                          // d
      Entry<String, String> entry = entries.next();
      String key = entry.getKey();
      String value = entry.getValue();
      if (isTimestampKey(key) && isAbandoned(value)) {
        String cookieId = extractCookieId(key);
        String cart = store.get(asCartKey(cookieId));
        sendEvent(collector, cookieId, cart);
        resetShopper(cookieId);
      }
    }
  }

  private static String asTimestampKey(String cookieId) {
    return cookieId + "-ts";
  }

  private static boolean isTimestampKey(String key) {
    return key.endsWith("-ts");
  }

  private static String extractCookieId(String key) {
    return key.substring(0, key.lastIndexOf('-'));
  }

  private static String asCartKey(String cookieId) {
    return cookieId + "-cart";
  }

  private void resetShopper(String cookieId) {
    store.delete(asTimestampKey(cookieId));
    store.delete(asCartKey(cookieId));    
  }

  private boolean isAbandoned(String timestamp) {
    DateTime ts = EVENT_DTF.parseDateTime(timestamp);
    DateTime cutoff = new DateTime(DateTimeZone.UTC)
      .minusSeconds(ABANDONED_AFTER_SECS);
    return ts.isBefore(cutoff);
  }

  private String updateCart(String cart, Map<String, Object> item) {     // e
    try {
      Collection<Map<String, Object>> items;
      if (cart == null) {
        items = new ArrayList<Map<String, Object>>();
      } else {
        items = MAPPER.readValue(cart,
          new TypeReference<Collection<Map<String, Object>>>() {});
      }
      items.add(item);
      return MAPPER.writeValueAsString(items);
    } catch (IOException iae) {
      return cart;
    }
  }

  private void sendEvent(MessageCollector collector,
    String cookieId, String cart) {                                      // f

    Map<String, String> subject = new HashMap<String, String>();
    subject.put("cookieId", cookieId);
    Map<String, String> context = new HashMap<String, String>();
    context.put("timestamp", EVENT_DTF.print(new DateTime(DateTimeZone.UTC)));
    Map<String, Object> directObject = new HashMap<String, Object>();

    Collection<Map<String, Object>> items;
    try {
      items = MAPPER.readValue(cart,
        new TypeReference<Collection<Map<String, Object>>>() {});
    } catch (IOException iae) {
      items = new ArrayList<Map<String, Object>>();
    }
    directObject.put("items", items);

    Map<String, Object> event = new HashMap<String, Object>();    
    event.put("subject", subject);
    event.put("verb", "abandon");
    event.put("directObject", directObject);
    event.put("context", context);

    collector.send(new OutgoingMessageEnvelope(
      new SystemStream("kafka", "derived_events"), event));
  }

}
