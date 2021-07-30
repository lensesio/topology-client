package io.lenses.topology.client.kafka.metrics;

import io.lenses.topology.client.*;
import io.lenses.topology.client.metrics.Metrics;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MetricsPublishTaskTest {
    @Test
    public void publishMetricsTest() throws IOException, InterruptedException, ExecutionException {
        String intopic = "metrics-topic-test" + System.nanoTime();

        Topology topology = TopologyBuilder.start("sammyapp")
                .withTopic(intopic).withKeyType(DecoderType.INT).withValueType(DecoderType.JSON).asTable().endNode()
                .build();

        // setup topology client
        Properties producerProps = new Properties();
        producerProps.setProperty("bootstrap.servers", "PLAINTEXT://localhost:9092");
        producerProps.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        producerProps.setProperty(TopologyClient.PUBLISH_INTERVAL_CONFIG_KEY, "1000");
        TopologyClient client = KafkaTopologyClient.create(producerProps);

        // create a producer that will be instrumented
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(producerProps);

        AdminClient adminClient = AdminClient.create(producerProps);
        adminClient.createTopics(Collections.singleton(new NewTopic(intopic, 1, (short) 1))).all().get();
        // register topology and the producer of hte data
        client.register(topology);
        client.register("sammyapp", intopic, new KafkaMetricsBuilder(producer));

        // setup a consumer to listen to the metrics topic
        Properties consumerProps = new Properties();
        consumerProps.setProperty("bootstrap.servers", "PLAINTEXT://localhost:9092");
        consumerProps.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        consumerProps.setProperty("group.id", "metrics-test");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(consumerProps);
        consumer.assign(consumer.partitionsFor(KafkaPublisher.DEFAULT_METRICS_TOPIC_NAME).stream().map(partitionInfo -> new TopicPartition(partitionInfo.topic(), partitionInfo.partition())).collect(Collectors.toList()));

        // publish some data using the instrumented producer so that we get some data on the metrics topic
        for (int k = 1; k < 100; ++k) {
            producer.send(new ProducerRecord<>(intopic, "testy"));
        }
        producer.flush();
        // should be metrics objects now on the metrics topic

        List<ConsumerRecord<String, String>> records = new ArrayList<>();
        consumer.poll(Duration.ofMillis(10000)).forEach(records::add);

        // we publish metrics in this test every 1 second, and we published messages for 5 seconds, so should
        // be around 4/5 metrics messages
        assertEquals(1, records.size());

        Metrics metrics = JacksonSupport.mapper.readValue(records.get(0).value(), Metrics.class);
        assertEquals(metrics.getAppName(), "sammyapp");
        assertTrue(metrics.getBytesSentPerSecond() > 0.0);
        assertTrue(metrics.getMessagesSentPerSecond() > 0.0);
        assertTrue(metrics.getMessageSentTotal() > 0);
    }
}
