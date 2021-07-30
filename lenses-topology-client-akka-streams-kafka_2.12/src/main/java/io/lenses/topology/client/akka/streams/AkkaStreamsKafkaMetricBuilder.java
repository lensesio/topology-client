package io.lenses.topology.client.akka.streams;

import akka.kafka.javadsl.Consumer;
import io.lenses.topology.client.kafka.metrics.KafkaMetricsBuilder;
import io.lenses.topology.client.metrics.Metrics;
import io.lenses.topology.client.metrics.MetricsBuilder;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class AkkaStreamsKafkaMetricBuilder implements MetricsBuilder {

    private final Consumer.Control control;
    private AtomicReference<Map<MetricName, Metric>> metrics = new AtomicReference<>(null);

    public AkkaStreamsKafkaMetricBuilder(Consumer.Control control) {
        this.control = control;
        requestMetrics();
    }

    @Override
    public Metrics build(String appName, String topic) {
        if (metrics.get() == null) {
            // the metrics are not available immediately, so we must retry if they have
            // not yet been resolved
            requestMetrics();
            return new Metrics(appName, topic);
        } else {
            return new KafkaMetricsBuilder(metrics.get()).build(appName, topic);
        }
    }

    private void requestMetrics() {
        control.getMetrics().whenComplete((metricNameMetricMap, throwable) ->
                metrics.compareAndSet(null, metricNameMetricMap)
        );
    }
}