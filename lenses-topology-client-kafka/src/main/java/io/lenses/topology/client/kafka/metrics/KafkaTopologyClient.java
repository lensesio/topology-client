package io.lenses.topology.client.kafka.metrics;

import io.lenses.topology.client.TopologyClient;

import java.time.Duration;
import java.util.Properties;

public class KafkaTopologyClient {
  public static TopologyClient create(Properties props) {
    final String publishInterval = props.getProperty(TopologyClient.PUBLISH_INTERVAL_CONFIG_KEY, "5000");
    try {
      final long durationMs = Long.parseLong(publishInterval);
      return new TopologyClient(new KafkaPublisher(props), Duration.ofMillis(durationMs));
    } catch (NumberFormatException ex) {
      throw new IllegalArgumentException("Invalid argument [props]. The entry [" + TopologyClient.PUBLISH_INTERVAL_CONFIG_KEY +
          "] has an invalid value of [" + publishInterval + "].");
    }
  }
}