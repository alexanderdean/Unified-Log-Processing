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
package nile.tasks;

import java.util.Collection;
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

public class LookToBookStreamTask
  implements StreamTask, InitableTask, WindowableTask {

  private KeyValueStore<String, Integer> store;
  private Set<String> products = new HashSet<String>();

  public void init(Config config, TaskContext context) {
    this.store = (KeyValueStore<String, Integer>)
      context.getStore("nile-looktobook");
  }

  @SuppressWarnings("unchecked")
  @Override
  public void process(IncomingMessageEnvelope envelope,
    MessageCollector collector, TaskCoordinator coordinator) {

    Map<String, Object> event = (Map<String, Object>) envelope.getMessage();
    String verb = (String) event.get("verb");
    
    if (verb.equals("view")) {                                           // a
      String productSku = (String) ((Map<String, Object>)
        event.get("directObject")).get("productSku");
      incrementViews(productSku);

    } else if (verb.equals("place")) {                                   // b
      Collection<Map<String, Object>> items = (Collection<Map<String, Object>>)
        ((Map<String, Object>) event.get("directObject")).get("items");
      for (Map<String, Object> item : items) {
        String productSku = (String) item.get("productSku");
        Integer quantity = (Integer) item.get("quantity");
        incrementPurchases(productSku, quantity);
      }
    }
  }

  @Override
  public void window(MessageCollector collector,
    TaskCoordinator coordinator) {

    Map<String, HashMap<String, Integer>> allCounts =
      new HashMap<String, HashMap<String, Integer>>();

    for (String product : products) {                                    // c
      HashMap<String, Integer> counts = new HashMap<String, Integer>();
      counts.put("views", store.get(asViewKey(product)));
      counts.put("purchases", store.get(asPurchaseKey(product)));
      allCounts.put(product, counts);
    }

    collector.send(new OutgoingMessageEnvelope(
      new SystemStream("kafka", "looktobook_stats"), allCounts));

    products.clear();
  }

  private static String asViewKey(String productSku) {                          // d
    return productSku + "-views";
  }

  private static String asPurchaseKey(String productSku) {
    return productSku + "-purchases";
  }

  private void incrementViews(String productSku) {                       // e
    String viewKey = asViewKey(productSku);
    Integer viewsLifetime = store.get(viewKey);
    if (viewsLifetime == null) viewsLifetime = 0;
    
    store.put(viewKey, viewsLifetime + 1);
    products.add(productSku);
  }

  private void incrementPurchases(String productSku, Integer quantity) {    // e
    String purchaseKey = asPurchaseKey(productSku);
    Integer purchasesLifetime = store.get(purchaseKey);
    if (purchasesLifetime == null) purchasesLifetime = 0;
    
    store.put(purchaseKey, purchasesLifetime + quantity);
    products.add(productSku);
  }
}
