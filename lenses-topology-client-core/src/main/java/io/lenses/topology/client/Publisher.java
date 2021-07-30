package io.lenses.topology.client;

import io.lenses.topology.client.metrics.Metrics;

import java.io.IOException;

/**
 * Responsible for publishing instances of {@link Topology}
 * and metrics to a storage mechanism.
 */
public interface Publisher {
    void publish(Topology topology) throws IOException;

    void publish(Metrics metrics) throws IOException;

    void flush();

    void delete(String appName);

    void close();
}
