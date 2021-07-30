package io.lenses.topology.client.kafka.metrics;

import io.lenses.topology.client.Publisher;
import org.apache.kafka.clients.consumer.ConsumerGroupMetadata;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.Metric;
import org.apache.kafka.common.MetricName;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.ProducerFencedException;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * An implementation of {@link Publisher} which will invoke a supplied
 * callback when the underlying producer is closed.
 */
public class CallbackProducer<K, V> implements Producer<K, V> {

    private final Producer<K, V> producer;
    private final Runnable callback;

    public CallbackProducer(Producer<K, V> producer, Runnable callback) {
        this.producer = producer;
        this.callback = callback;
    }

    @Override
    public void initTransactions() {
        producer.initTransactions();
    }

    @Override
    public void beginTransaction() throws ProducerFencedException {
        producer.beginTransaction();
    }

    @Override
    public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> map, String s) throws ProducerFencedException {
        producer.sendOffsetsToTransaction(map, s);
    }

    @Override
    public void sendOffsetsToTransaction(Map<TopicPartition, OffsetAndMetadata> offsets, ConsumerGroupMetadata groupMetadata) throws ProducerFencedException {
        producer.sendOffsetsToTransaction(offsets, groupMetadata);
    }

    @Override
    public void commitTransaction() throws ProducerFencedException {
        producer.commitTransaction();
    }

    @Override
    public void abortTransaction() throws ProducerFencedException {
        producer.abortTransaction();
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<K, V> producerRecord) {
        return producer.send(producerRecord);
    }

    @Override
    public Future<RecordMetadata> send(ProducerRecord<K, V> producerRecord, Callback callback) {
        return producer.send(producerRecord, callback);
    }

    @Override
    public void flush() {
        producer.flush();
    }

    @Override
    public List<PartitionInfo> partitionsFor(String s) {
        return producer.partitionsFor(s);
    }

    @Override
    public Map<MetricName, ? extends Metric> metrics() {
        return producer.metrics();
    }

    @Override
    public void close() {
        producer.close();
        callback.run();
    }

    @Override
    public void close(long l, TimeUnit timeUnit) {
        producer.close(l, timeUnit);
        callback.run();
    }

    @Override
    public void close(Duration timeout) {
        producer.close(timeout);
        callback.run();
    }
}
