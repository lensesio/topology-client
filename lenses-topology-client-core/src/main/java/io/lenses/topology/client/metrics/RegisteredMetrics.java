package io.lenses.topology.client.metrics;

import java.util.Objects;

public class RegisteredMetrics {

    private final String appName;
    private final String topic;
    private final MetricsBuilder builder;

    public RegisteredMetrics(String appName, String topic, MetricsBuilder builder) {
        this.appName = appName;
        this.topic = topic;
        this.builder = builder;
    }

    public MetricsBuilder getMetricsBuilder() {
        return builder;
    }

    public String getAppName() {
        return appName;
    }

    public String getTopic() {
        return topic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisteredMetrics that = (RegisteredMetrics) o;
        return Objects.equals(appName, that.appName) &&
                Objects.equals(topic, that.topic) &&
                Objects.equals(builder, that.builder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appName, topic, builder);
    }
}
