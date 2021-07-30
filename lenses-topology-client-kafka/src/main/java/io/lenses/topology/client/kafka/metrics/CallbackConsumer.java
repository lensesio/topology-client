package io.lenses.topology.client.kafka.metrics;

import io.lenses.topology.client.Publisher;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * An implementation of {@link Publisher} which will invoke a supplied
 * callback when the underlying producer is closed.
 */
public class CallbackConsumer<K, V> implements Consumer<K, V> {

  private final Consumer<K, V> consumer;
  private final Runnable callback;

  public CallbackConsumer(Consumer<K, V> consumer, Runnable callback) {
    this.consumer = consumer;
    this.callback = callback;
  }

  @Override
  public Set<TopicPartition> assignment() {
    return consumer.assignment();
  }

  @Override
  public Set<String> subscription() {
    return consumer.subscription();
  }

  @Override
  public void subscribe(Collection<String> collection) {
    consumer.subscribe(collection);
  }

  @Override
  public void subscribe(Collection<String> collection, ConsumerRebalanceListener consumerRebalanceListener) {
    consumer.subscribe(collection, consumerRebalanceListener);
  }

  @Override
  public void assign(Collection<TopicPartition> collection) {
    consumer.assign(collection);
  }

  @Override
  public void subscribe(Pattern pattern, ConsumerRebalanceListener consumerRebalanceListener) {
    consumer.subscribe(pattern, consumerRebalanceListener);
  }

  @Override
  public void subscribe(Pattern pattern) {
    consumer.subscribe(pattern);
  }

  @Override
  public void unsubscribe() {
    consumer.unsubscribe();
  }

  @Override
  public ConsumerRecords<K, V> poll(long l) {
    return consumer.poll(l);
  }

  @Override
  public ConsumerRecords<K, V> poll(Duration timeout) {
    return consumer.poll(timeout);
  }

  @Override
  public void commitSync() {
    consumer.commitSync();
  }

  @Override
  public void commitSync(Duration timeout) {
    consumer.commitSync(timeout);
  }

  @Override
  public void commitSync(Map<TopicPartition, OffsetAndMetadata> map) {
    consumer.commitSync(map);
  }

  @Override
  public void commitSync(Map<TopicPartition, OffsetAndMetadata> offsets, Duration timeout) {
    consumer.commitSync(offsets, timeout);
  }

  @Override
  public void commitAsync() {
    consumer.commitAsync();
  }

  @Override
  public void commitAsync(OffsetCommitCallback offsetCommitCallback) {
    consumer.commitAsync(offsetCommitCallback);
  }

  @Override
  public void commitAsync(Map<TopicPartition, OffsetAndMetadata> map, OffsetCommitCallback offsetCommitCallback) {
    consumer.commitAsync(map, offsetCommitCallback);
  }

  @Override
  public void seek(TopicPartition topicPartition, long l) {
    consumer.seek(topicPartition, l);
  }

  @Override
  public void seek(TopicPartition partition, OffsetAndMetadata offsetAndMetadata) {
    consumer.seek(partition, offsetAndMetadata);
  }

  @Override
  public void seekToBeginning(Collection<TopicPartition> collection) {
    consumer.seekToBeginning(collection);
  }

  @Override
  public void seekToEnd(Collection<TopicPartition> collection) {
    consumer.seekToEnd(collection);
  }

  @Override
  public long position(TopicPartition topicPartition) {
    return consumer.position(topicPartition);
  }

  @Override
  public long position(TopicPartition partition, Duration timeout) {
    return consumer.position(partition, timeout);
  }

  @Override
  public OffsetAndMetadata committed(TopicPartition topicPartition) {
    return consumer.committed(topicPartition);
  }

  @Override
  public OffsetAndMetadata committed(TopicPartition partition, Duration timeout) {
    return consumer.committed(partition, timeout);
  }

  @Override
  public Map<TopicPartition, OffsetAndMetadata> committed(Set<TopicPartition> partitions) {
    return consumer.committed(partitions);
  }

  @Override
  public Map<TopicPartition, OffsetAndMetadata> committed(Set<TopicPartition> partitions, Duration timeout) {
    return consumer.committed(partitions, timeout);
  }

  @Override
  public Map<MetricName, ? extends Metric> metrics() {
    return consumer.metrics();
  }

  @Override
  public List<PartitionInfo> partitionsFor(String s) {
    return consumer.partitionsFor(s);
  }

  @Override
  public List<PartitionInfo> partitionsFor(String topic, Duration timeout) {
    return consumer.partitionsFor(topic, timeout);
  }

  @Override
  public Map<String, List<PartitionInfo>> listTopics() {
    return consumer.listTopics();
  }

  @Override
  public Map<String, List<PartitionInfo>> listTopics(Duration timeout) {
    return consumer.listTopics(timeout);
  }

  @Override
  public Set<TopicPartition> paused() {
    return consumer.paused();
  }

  @Override
  public void pause(Collection<TopicPartition> collection) {
    consumer.pause(collection);
  }

  @Override
  public void resume(Collection<TopicPartition> collection) {
    consumer.resume(collection);
  }

  @Override
  public Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(Map<TopicPartition, Long> map) {
    return consumer.offsetsForTimes(map);
  }

  @Override
  public Map<TopicPartition, OffsetAndTimestamp> offsetsForTimes(Map<TopicPartition, Long> timestampsToSearch, Duration timeout) {
    return consumer.offsetsForTimes(timestampsToSearch, timeout);
  }

  @Override
  public Map<TopicPartition, Long> beginningOffsets(Collection<TopicPartition> collection) {
    return consumer.beginningOffsets(collection);
  }

  @Override
  public Map<TopicPartition, Long> beginningOffsets(Collection<TopicPartition> partitions, Duration timeout) {
    return consumer.beginningOffsets(partitions, timeout);
  }

  @Override
  public Map<TopicPartition, Long> endOffsets(Collection<TopicPartition> collection) {
    return consumer.endOffsets(collection);
  }

  @Override
  public Map<TopicPartition, Long> endOffsets(Collection<TopicPartition> partitions, Duration timeout) {
    return consumer.endOffsets(partitions, timeout);
  }

  @Override
  public ConsumerGroupMetadata groupMetadata() {
    return consumer.groupMetadata();
  }

  @Override
  public void enforceRebalance() {
    consumer.enforceRebalance();
  }

  @Override
  public void close() {
    consumer.close();
    callback.run();
  }

  @Override
  public void close(long l, TimeUnit timeUnit) {
    consumer.close(l, timeUnit);
    callback.run();
  }

  @Override
  public void close(Duration timeout) {
    consumer.close(timeout);
    callback.run();
  }

  @Override
  public void wakeup() {
    consumer.wakeup();
  }
}
