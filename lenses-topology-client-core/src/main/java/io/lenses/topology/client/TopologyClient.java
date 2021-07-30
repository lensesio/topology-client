package io.lenses.topology.client;

import io.lenses.topology.client.metrics.MetricsBuilder;
import io.lenses.topology.client.metrics.MetricsPublishTask;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.NetworkInterface;
import java.time.Duration;
import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TopologyClient implements AutoCloseable {
  public static final String PUBLISH_INTERVAL_CONFIG_KEY = "lenses.publish.interval.ms";

  public static String getPid() {
    String name = ManagementFactory.getRuntimeMXBean().getName();
    return name.split("@")[0];
  }

  public static String getMac() {

    try {
      Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
      while (interfaces.hasMoreElements()) {
        byte[] mac = interfaces.nextElement().getHardwareAddress();
        if (mac != null) {
          StringBuilder sb = new StringBuilder(18);
          for (byte b : mac) {
            if (sb.length() > 0)
              sb.append(':');
            sb.append(String.format("%02x", b));
          }
          return sb.toString();
        }
      }
    } catch (IOException ignored) {
    }
    return "";
  }

  private final Publisher publisher;
  private final ExecutorService executor = Executors.newSingleThreadExecutor();
  private final MetricsPublishTask task;

  public TopologyClient(Publisher publisher, Duration publishInterval) {
    this.publisher = publisher;
    task = new MetricsPublishTask(publisher, publishInterval);
    executor.submit(task);
  }

  public void register(Topology topology) throws IOException {
    topology.setPid(getPid());
    topology.setMachine(getMac());
    publisher.publish(topology);
  }

  public void register(String appName, String topic, MetricsBuilder builder) {
    task.register(appName, topic, builder);
  }

  public void unregister(MetricsBuilder builder) {
    task.unregister(builder);
  }

  public void unregister(String appName, String topic) {
    task.unregister(appName, topic);
  }

  public void flush() {
    publisher.flush();
  }

  public void close() {
    publisher.close();
    executor.shutdownNow();
  }

  public void delete(Topology topology) {
    delete(topology.getAppName());
  }

  private void delete(String appName) {
    publisher.delete(appName);
  }
}
