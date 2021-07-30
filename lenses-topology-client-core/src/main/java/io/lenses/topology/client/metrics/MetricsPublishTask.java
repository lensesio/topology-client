package io.lenses.topology.client.metrics;

import io.lenses.topology.client.Publisher;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Instances of this task will periodically publish metrics for any
 * registered app and topic. Each registered app is required to provide
 * an instance of of {@link MetricsBuilder}.
 */
public class MetricsPublishTask implements Runnable {

    private final Duration publishInterval;
    private final CopyOnWriteArrayList<RegisteredMetrics> registeredMetrics = new CopyOnWriteArrayList<>();
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final Publisher publisher;

    public MetricsPublishTask(Publisher publisher, Duration publishInterval) {
        this.publisher = publisher;
        this.publishInterval = publishInterval;
    }

    @Override
    public void run() {
        while (running.get()) {
            try {
                Thread.sleep(publishInterval.toMillis());
                publish();
                if (Thread.interrupted())
                    throw new InterruptedException();
            } catch (InterruptedException e) {
                running.set(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void register(String appName, String topic, MetricsBuilder builder) {
        registeredMetrics.add(new RegisteredMetrics(appName, topic, builder));
    }

    public void unregister(String appName, String topic) {
        registeredMetrics.stream()
                .filter(rm -> rm.getAppName().equals(appName) && rm.getTopic().equals(topic))
                .findFirst()
                .ifPresent(registeredMetrics::remove);
    }

    public void unregister(MetricsBuilder builder) {
        registeredMetrics.stream()
                .filter(rm -> rm.getMetricsBuilder() == builder)
                .findFirst()
                .ifPresent(registeredMetrics::remove);
    }

    private synchronized void publish() throws IOException {
        for (RegisteredMetrics rm : registeredMetrics) {
            Metrics metrics = rm.getMetricsBuilder().build(rm.getAppName(), rm.getTopic());
            if(metrics != null) {
              publisher.publish(metrics);
            }
        }
        publisher.flush();
    }
}
