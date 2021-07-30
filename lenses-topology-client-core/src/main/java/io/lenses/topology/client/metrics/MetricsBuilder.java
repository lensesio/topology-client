package io.lenses.topology.client.metrics;

public interface MetricsBuilder {
    Metrics build(String appName, String topic);
}
