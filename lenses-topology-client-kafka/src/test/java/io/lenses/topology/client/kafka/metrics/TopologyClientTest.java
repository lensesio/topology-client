package io.lenses.topology.client.kafka.metrics;

import io.lenses.topology.client.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TopologyClientTest {
    @Test
    public void testSendResponse() throws IOException {
        Topology topology = TopologyBuilder.start("sammyapp")
                .withNode("node1", NodeType.JOIN).withDescription("my node").withKeyType(DecoderType.INT).withValueType(DecoderType.JSON).asTable().endNode()
                .withNode("node2", NodeType.AGGREGATE).withDescription("super node").withValueType(DecoderType.STRING).withParent("node1").asTable().endNode()
                .build();

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "PLAINTEXT://localhost:9092");
        props.setProperty("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.setProperty("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        TopologyClient client = KafkaTopologyClient.create(props);
        client.register(topology);

        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("auto.offset.reset", "earliest");
        props.setProperty("group.id", UUID.randomUUID().toString().replace("-", ""));

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton(KafkaPublisher.DEFAULT_TOPOLOGY_TOPIC_NAME));
        ConsumerRecord<String, String> record = consumer.poll(Duration.ofMillis(2000)).iterator().next();
        assertEquals(record.value(), "{\"appType\":\"Other\",\"appName\":\"sammyapp\",\"pid\":\"211411\",\"machine\":\"82:b3:79:79:9e:07\",\"nodes\":[{\"name\":\"node1\",\"description\":\"my node\",\"type\":\"JOIN\",\"representation\":\"TABLE\",\"keyType\":\"INT\",\"valueType\":\"JSON\",\"parents\":[]},{\"name\":\"node2\",\"description\":\"super node\",\"type\":\"AGGREGATE\",\"representation\":\"TABLE\",\"valueType\":\"STRING\",\"parents\":[\"node1\"]}],\"topics\":[],\"description\":\"sammyapp:\"}");
    }
}
