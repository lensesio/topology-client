package io.lenses.topology.client.kafka.metrics;

import io.lenses.topology.client.JacksonSupport;
import io.lenses.topology.client.Publisher;
import io.lenses.topology.client.Topology;
import io.lenses.topology.client.metrics.Metrics;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.TopicConfig;

import java.io.IOException;
import java.util.Properties;

public class KafkaPublisher implements Publisher {

  public static final String DEFAULT_TOPOLOGY_TOPIC_NAME = "__topology";
  public static final String DEFAULT_METRICS_TOPIC_NAME = "__topology__metrics";

  public static final String TOPOLOGY_TOPIC_CONFIG_KEY = "lenses.topics.topology";
  public static final String METRIC_TOPIC_CONFIG_KEY = "lenses.topics.metrics";


  private final KafkaProducer<String, String> producer;
  private final String topologyTopicName;
  private final String metricsTopicName;

  public KafkaPublisher(Properties props) {

    this.topologyTopicName = props.getProperty(TOPOLOGY_TOPIC_CONFIG_KEY, DEFAULT_TOPOLOGY_TOPIC_NAME);
    this.metricsTopicName = props.getProperty(METRIC_TOPIC_CONFIG_KEY, DEFAULT_METRICS_TOPIC_NAME);

    Properties producerProps = new Properties();
    producerProps.putAll(props);
    producerProps.put("key.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
    producerProps.put("value.serializer", org.apache.kafka.common.serialization.StringSerializer.class);
    producerProps.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT);
    producer = new KafkaProducer<>(producerProps);
  }

  @Override
  public void publish(Topology topology) throws IOException {
    String value = JacksonSupport.mapper.writeValueAsString(topology);
    producer.send(new ProducerRecord<>(topologyTopicName, topology.getAppName(), value));
  }

  @Override
  public void publish(Metrics metrics) throws IOException {
    String value = JacksonSupport.mapper.writeValueAsString(metrics);
    ProducerRecord<String, String> record = new ProducerRecord<>(metricsTopicName, value);
    producer.send(record, (recordMetadata, e) -> {
      if (e != null) System.out.println(e);
    });
  }

  @Override
  public void flush() {
    producer.flush();
  }

  @Override
  public void delete(String appName) {
    producer.send(new ProducerRecord<>(topologyTopicName, appName, null));
  }

  @Override
  public void close() {
    producer.close();
  }
}
