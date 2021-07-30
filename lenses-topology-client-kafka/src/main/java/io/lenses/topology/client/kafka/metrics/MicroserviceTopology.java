package io.lenses.topology.client.kafka.metrics;

import io.lenses.topology.client.*;
import io.lenses.topology.client.metrics.MetricsBuilder;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;
import java.util.stream.Stream;

public class MicroserviceTopology {

    /**
     * Creates a {@link TopologyClient} which registers the application topology with Lenses and sends the metrics associated with {@link Producer} and {@link Consumer}
     * instances provided
     *
     * @param appName           - The name of your microservice application
     * @param producerTopicsMap - A map of producers to the topics they send Kafka records to
     * @param consumerTopicsMap - A map of consumers and the topics they read message from
     * @param properties        - An instance of {@link Properties}
     * @return An instance of {@link TopologyClient} which will publish the metrics for the topology to Lenses
     * @throws IOException Throws IOException
     */
    public static TopologyClient create(final String appName,
                                        final Map<Producer<?, ?>, List<String>> producerTopicsMap,
                                        final Map<Consumer<?, ?>, List<String>> consumerTopicsMap,
                                        final Properties properties) throws IOException {
        if (appName == null)
            throw new NullPointerException("Invalid parameter [appName]. Expecting non-null and non-empty application name.");
        if (appName.trim().isEmpty())
            throw new IllegalArgumentException("Invalid parameter [appName]. Expecting non-empty application name.");
        if (producerTopicsMap == null) throw new NullPointerException("Invalid parameter [producerTopicsMap].");
        if (consumerTopicsMap == null) throw new NullPointerException("Invalid parameter [consumerTopicsMap].");
        if (properties == null) throw new NullPointerException("Invalid parameter [properties].");
        final TopologyClient client = KafkaTopologyClient.create(properties);
        buildAndRegisterTopology(client, appName, producerTopicsMap, consumerTopicsMap);
        producerTopicsMap.forEach((key, value) -> registerMetrics(client, appName, value, key));
        consumerTopicsMap.forEach((key, value) -> registerMetrics(client, appName, value, key));
        return client;
    }

    /**
     * Creates a topology client which publishes the topology associated with a Kafka producer and the metrics for it to a Kafka topic
     * which Lenses reads.
     *
     * @param appName    - The name of your application/microservice
     * @param producer   - The instance of the {@link Producer}
     * @param topics     - A list of topics the producer publishes records to
     * @param properties - An instance of {@link Properties} used to create the producer instance
     * @return An instance of {@link TopologyClient} which monitors the producer metrics and sends them for Lenses to pick up.
     * @throws IOException - Throws IOException
     */
    public static TopologyClient fromProducer(final String appName, final Producer<?, ?> producer, final List<String> topics, final Properties properties) throws IOException {
        return fromProducer(appName, producer, topics, properties, DecoderType.BYTES, DecoderType.BYTES);
    }

    /**
     * Creates a topology client which publishes the topology associated with a Kafka producer and the metrics for it to a Kafka topic
     * which Lenses reads.
     *
     * @param appName      - The name of your application/microservice
     * @param producer     - The instance of the {@link Producer}
     * @param topics       - A list of topics the producer publishes records to
     * @param properties   - An instance of {@link Properties} used to create the producer instance
     * @param keyEncoder   - Represents the format of a Kafka record key sent by producer
     * @param valueEncoder - Represents the format of a Kafka record value sent by producer
     * @return An instance of {@link TopologyClient} which monitors the producer metrics and sends them for Lenses to pick up.
     * @throws IOException - Throws IOException
     */
    public static TopologyClient fromProducer(final String appName, final Producer<?, ?> producer, final List<String> topics, final Properties properties, final DecoderType keyEncoder, final DecoderType valueEncoder) throws IOException {
        if (appName == null)
            throw new NullPointerException("Invalid parameter [appName]. Expecting non-null and non-empty application name.");
        if (appName.trim().isEmpty())
            throw new IllegalArgumentException("Invalid parameter [appName]. Expecting non-empty application name.");
        if (topics == null)
            throw new NullPointerException("Invalid parameter [topics]. Expecting non-null and non-empty list of topics.");
        if (topics.isEmpty())
            throw new IllegalArgumentException("Invalid parameter [topics]. Expecting non-empty list of topics.");
        if (producer == null)
            throw new NullPointerException("Invalid parameter [producer]. Expecting non-null instance of KafkaProducer.");

        final TopologyClient client = KafkaTopologyClient.create(properties);
        buildAndRegisterProducerTopology(client, appName, topics, keyEncoder, valueEncoder);
        registerMetrics(client, appName, topics, producer);
        return client;
    }

    /**
     * Creates a topology client which publishes the topology associated with a Kafka producer and the metrics for it to a Kafka topic
     * which Lenses reads.
     *
     * @param appName    - The name of your application/microservice
     * @param consumer   - The instance of the {@link Consumer}
     * @param topics     - A list of topics the producer publishes records to
     * @param properties - An instance of {@link Properties} used to create the producer instance
     * @return An instance of {@link TopologyClient} which monitors the producer metrics and sends them for Lenses to pick up.
     * @throws IOException Throws IOException
     */
    public static TopologyClient fromConsumer(final String appName, final Consumer<?, ?> consumer, final List<String> topics, final Properties properties) throws IOException {
        return fromConsumer(appName, consumer, topics, properties, DecoderType.BYTES, DecoderType.BYTES);
    }

    /**
     * Creates a topology client which publishes the topology associated with a Kafka producer and the metrics for it to a Kafka topic
     * which Lenses reads.
     *
     * @param appName      - The name of your application/microservice
     * @param consumer     - The instance of the {@link Consumer}
     * @param topics       - A list of topics the producer publishes records to
     * @param properties   - An instance of {@link Properties} used to create the producer instance
     * @param keyEncoder   - Represents the format of a Kafka record key sent by producer
     * @param valueEncoder - Represents the format of a Kafka record value sent by producer
     * @return An instance of {@link TopologyClient} which monitors the producer metrics and sends them for Lenses to pick up.
     * @throws IOException Throws IOException
     */
    public static TopologyClient fromConsumer(final String appName, final Consumer<?, ?> consumer, final List<String> topics, final Properties properties, final DecoderType keyEncoder, final DecoderType valueEncoder) throws IOException {
        if (appName == null)
            throw new NullPointerException("Invalid parameter [appName]. Expecting non-null and non-empty application name.");
        if (appName.trim().isEmpty())
            throw new IllegalArgumentException("Invalid parameter [appName]. Expecting non-empty application name.");
        if (topics == null)
            throw new NullPointerException("Invalid parameter [topics]. Expecting non-null and non-empty list of topics.");
        if (topics.isEmpty())
            throw new IllegalArgumentException("Invalid parameter [topics]. Expecting non-empty list of topics.");
        if (consumer == null)
            throw new NullPointerException("Invalid parameter [producer]. Expecting non-null instance of KafkaConsumer.");

        final TopologyClient client = KafkaTopologyClient.create(properties);
        buildAndRegisterConsumerTopology(client, appName, topics, keyEncoder, valueEncoder);
        registerMetrics(client, appName, topics, consumer);
        return client;
    }

    /**
     * Registers the metrics builder for each Kafka topic involved
     *
     * @param client   - An instance of {@link TopologyClient} which will publish the metrics
     * @param appName  -The name of your application/microservice
     * @param topics   - The list of Kafka topics involved
     * @param producer - An instance of {@link Producer} which is monitored to extract metrics
     */
    private static void registerMetrics(TopologyClient client, String appName, List<String> topics, Producer<?, ?> producer) {
        final MetricsBuilder metricsBuilder = new KafkaMetricsBuilder(producer);
        for (String topic : topics) {
            client.register(appName, topic, metricsBuilder);
        }
    }


    /**
     * Registers the metrics builder for each Kafka topic involved
     *
     * @param client   - An instance of {@link TopologyClient} which will publish the metrics
     * @param appName  -The name of your application/microservice
     * @param topics   - The list of Kafka topics involved
     * @param consumer - An instance of {@link Consumer} which is monitored to extract metrics
     */
    private static void registerMetrics(TopologyClient client, String appName, List<String> topics, Consumer<?, ?> consumer) {
        final MetricsBuilder metricsBuilder = new KafkaMetricsBuilder(consumer);
        for (String topic : topics) {
            client.register(appName, topic, metricsBuilder);
        }
    }


    /**
     * Creates the topology for the simple producer application/microservice
     *
     * @param client    - An instance of {@link TopologyClient} used to publish the topology
     * @param appName   - The application/microservice name
     * @param topics    - The list of Kafka topics the application sends records to
     * @param keyType   - Represents the format of a Kafka record key sent by the application
     * @param valueType - Represents the format of a Kafka record value sent by the application
     * @throws IOException Throws IOException
     */
    private static void buildAndRegisterProducerTopology(TopologyClient client,
                                                         String appName,
                                                         List<String> topics,
                                                         DecoderType keyType,
                                                         DecoderType valueType) throws IOException {
        TopologyBuilder builder = TopologyBuilder.start(AppType.KafkaProducer, appName)
                .withNode(appName, NodeType.STREAM)
                .withKeyType(keyType)
                .withValueType(valueType)
                .withRepresentation(Representation.STREAM)
                .endNode();


        for (String topic : topics) {
            builder = builder.withTopic(topic)
                    .withRepresentation(Representation.TABLE)
                    .withParent(appName)
                    .endNode();
        }
        final Topology topology = builder.build();
        client.register(topology);
    }

    /**
     * Creates the topology for the simple consumer application/microservice
     *
     * @param client    - An instance of {@link TopologyClient} used to publish the topology
     * @param appName   - The application/microservice name
     * @param topics    - The list of Kafka topics the application reads records from
     * @param keyType   - Represents the format of a Kafka record key read by the application
     * @param valueType - Represents the format of a Kafka record value read by the application
     * @throws IOException Throws IOException
     */
    private static void buildAndRegisterConsumerTopology(TopologyClient client,
                                                         String appName,
                                                         List<String> topics,
                                                         DecoderType keyType,
                                                         DecoderType valueType) throws IOException {
        TopologyBuilder builder = TopologyBuilder.start(AppType.KafkaConsumer, appName)
                .withNode(appName, NodeType.STREAM)
                .withKeyType(keyType)
                .withValueType(valueType)
                .withRepresentation(Representation.STREAM)
                .withParents(topics)
                .endNode();


        for (String topic : topics) {
            builder = builder.withTopic(topic)
                    .withRepresentation(Representation.TABLE)
                    .endNode();
        }
        final Topology topology = builder.build();
        client.register(topology);
    }


    /**
     * Creates the topology for the simple consumer application/microservice
     *
     * @param client            - An instance of {@link TopologyClient} used to publish the topology
     * @param appName           - The application/microservice name
     * @param producerTopicsMap - A map of producer to the topics they publish to
     * @param consumerTopicsMap - A map of consumer and the topics they read from
     * @throws IOException
     */
    private static void buildAndRegisterTopology(TopologyClient client,
                                                 String appName,
                                                 final Map<Producer<?, ?>, List<String>> producerTopicsMap,
                                                 final Map<Consumer<?, ?>, List<String>> consumerTopicsMap) throws IOException {
        final TopologyBuilder builder = TopologyBuilder.start(AppType.Microservice, appName);

        //set the deps on the incoming topics
        TopologyBuilder.NodeBuilder nodeBuilder = builder.withNode(appName, NodeType.STREAM);
        consumerTopicsMap.values().forEach(nodeBuilder::withParents);
        nodeBuilder.withRepresentation(Representation.STREAM).endNode();

        producerTopicsMap.values().forEach(parentNodes -> {
            for (String topic : parentNodes) {
                builder.withNode(topic, NodeType.TOPIC).withRepresentation(Representation.TABLE).withParents(appName).endNode();
            }
        });

        consumerTopicsMap.values().stream().flatMap((Function<List<String>, Stream<String>>) Collection::stream)
                .forEach(topic -> builder.withTopic(topic).withRepresentation(Representation.TABLE).endNode());

        final Topology topology = builder.build();
        client.register(topology);
    }
}
