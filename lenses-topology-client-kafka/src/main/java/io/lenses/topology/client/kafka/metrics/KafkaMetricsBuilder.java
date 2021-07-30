package io.lenses.topology.client.kafka.metrics;

import io.lenses.topology.client.metrics.Metrics;
import io.lenses.topology.client.metrics.MetricsBuilder;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;

import java.util.Map;

public class KafkaMetricsBuilder implements MetricsBuilder {

    private final Map<MetricName, ? extends Metric> metricsMap;

    public KafkaMetricsBuilder(Map<MetricName, ? extends Metric> metricsMap) {
        this.metricsMap = metricsMap;
    }

    public KafkaMetricsBuilder(Producer<?, ?> producer) {
        this(producer.metrics());
    }

    public KafkaMetricsBuilder(Consumer<?, ?> consumer) {
        this(consumer.metrics());
    }

    @Override
    public Metrics build(String appName, String topic) {
        Metrics metrics = new Metrics(appName, topic);
        final boolean[] set = {false};
        metricsMap.entrySet().stream()
                .filter(entry -> entry.getKey() != null)
                .filter(entry -> entry.getKey().tags() != null)
                .filter(entry -> topic.equals(entry.getKey().tags().get("topic")))
                .forEach(entry -> {
                    switch (entry.getKey().name()) {
                        case "record-send-total":
                            set[0] = true;
                            metrics.setMessageSentTotal((long) entry.getValue().value());
                            break;
                        case "record-error-total":
                            set[0] = true;
                            metrics.setMessageErrorTotal((long) entry.getValue().value());
                            break;
                        case "byte-rate":
                            set[0] = true;
                            metrics.setBytesSentPerSecond(entry.getValue().value());
                            break;
                        case "record-send-rate":
                            set[0] = true;
                            metrics.setMessagesSentPerSecond(entry.getValue().value());
                            break;
                        case "records-consumed-total":
                            set[0] = true;
                            metrics.setMessageReceivedTotal((long) entry.getValue().value());
                            break;
                        case "bytes-consumed-rate":
                            set[0] = true;
                            metrics.setBytesReceivedPerSecond(entry.getValue().value());
                            break;
                        case "records-consumed-rate":
                            set[0] = true;
                            metrics.setMessagesReceivedPerSecond(entry.getValue().value());
                            break;
                    }
                });
        if (!set[0]) return null;
        return metrics;
    }
}
