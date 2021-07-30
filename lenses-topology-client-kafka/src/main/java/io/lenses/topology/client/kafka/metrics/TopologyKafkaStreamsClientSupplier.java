package io.lenses.topology.client.kafka.metrics;

import io.lenses.topology.client.Topology;
import io.lenses.topology.client.TopologyClient;
import io.lenses.topology.client.metrics.MetricsBuilder;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.streams.KafkaClientSupplier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An implementation of a KafkaClientSupplier to be used with KStreams that will
 * register metrics with the topology client.
 */
public class TopologyKafkaStreamsClientSupplier implements KafkaClientSupplier {

  private final TopologyClient client;
  private final Topology topology;

  public TopologyKafkaStreamsClientSupplier(TopologyClient client, Topology topology) {
    this.client = client;
    this.topology = topology;
  }

  @Override
  public AdminClient getAdminClient(Map<String, Object> config) {
    return AdminClient.create(config);
  }

  @Override
  public Producer<byte[], byte[]> getProducer(Map<String, Object> config) {
    KafkaProducer<byte[], byte[]> producer = new KafkaProducer<>(config, new ByteArraySerializer(), new ByteArraySerializer());

    List<MetricsBuilder> builders = new ArrayList<>();
    for (String topic : topology.getTopics()) {
      KafkaMetricsBuilder builder = new KafkaMetricsBuilder(producer);
      builders.add(builder);
      client.register(topology.getAppName(), topic, builder);
    }
    Runnable callback = () -> {
      for (MetricsBuilder builder : builders) {
        client.unregister(builder);
      }
    };
    return new CallbackProducer<>(producer, callback);
  }

  @Override
  public Consumer<byte[], byte[]> getConsumer(Map<String, Object> config) {
    KafkaConsumer<byte[], byte[]> consumer = new KafkaConsumer<>(config, new ByteArrayDeserializer(), new ByteArrayDeserializer());

    List<MetricsBuilder> builders = new ArrayList<>();
    for (String topic : topology.getTopics()) {
      KafkaMetricsBuilder builder = new KafkaMetricsBuilder(consumer);
      builders.add(builder);
      client.register(topology.getAppName(), topic, builder);
    }
    Runnable callback = () -> {
      for (MetricsBuilder builder : builders) {
        client.unregister(builder);
      }
    };
    return new CallbackConsumer<>(consumer, callback);
  }

  @Override
  public Consumer<byte[], byte[]> getRestoreConsumer(Map<String, Object> config) {
    return new KafkaConsumer<>(config, new ByteArrayDeserializer(), new ByteArrayDeserializer());
  }

  @Override
  public Consumer<byte[], byte[]> getGlobalConsumer(Map<String, Object> config) {
    return new KafkaConsumer<>(config, new ByteArrayDeserializer(), new ByteArrayDeserializer());
  }
}
