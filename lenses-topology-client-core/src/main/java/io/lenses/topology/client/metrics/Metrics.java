package io.lenses.topology.client.metrics;

import io.lenses.topology.client.TopologyClient;

public class Metrics {

    private final String appName;
    private final String pid;
    private final String machine;
    private final String topic;
    private double messagesSentPerSecond = 0;
    private double bytesSentPerSecond = 0;
    private long messageSentTotal = 0;
    private long messageErrorTotal = 0;
    private double messagesReceivedPerSecond = 0;
    private long messageReceivedTotal = 0;
    private double bytesReceivedPerSecond = 0;

    // for jackson
    Metrics() {
        this("", "", "", "");
    }

    public Metrics(String appName, String pid, String machine, String topic) {
        this.appName = appName;
        this.pid = pid;
        this.machine = machine;
        this.topic = topic;
    }

    public Metrics(String appName, String topic) {
        this(appName, TopologyClient.getPid(), TopologyClient.getMac(), topic);
    }

    public String getPid() {
        return pid;
    }

    public String getMachine() {
        return machine;
    }

    public String getTopic() {
        return topic;
    }

    public double getMessagesReceivedPerSecond() {
        return messagesReceivedPerSecond;
    }

    public long getMessageReceivedTotal() {
        return messageReceivedTotal;
    }

    public double getBytesReceivedPerSecond() {
        return bytesReceivedPerSecond;
    }

    public void setMessagesReceivedPerSecond(double messagesReceivedPerSecond) {
        this.messagesReceivedPerSecond = messagesReceivedPerSecond;
    }

    public void setMessageReceivedTotal(long messageReceivedTotal) {
        this.messageReceivedTotal = messageReceivedTotal;
    }

    public void setBytesReceivedPerSecond(double bytesReceivedPerSecond) {
        this.bytesReceivedPerSecond = bytesReceivedPerSecond;
    }

    public String getAppName() {
        return appName;
    }

    public double getMessagesSentPerSecond() {
        return messagesSentPerSecond;
    }

    public long getMessageSentTotal() {
        return messageSentTotal;
    }

    public long getMessageErrorTotal() {
        return messageErrorTotal;
    }

    public double getBytesSentPerSecond() {
        return bytesSentPerSecond;
    }

    public void setMessagesSentPerSecond(double messagesSentPerSecond) {
        this.messagesSentPerSecond = messagesSentPerSecond;
    }

    public void setMessageSentTotal(long messageSentTotal) {
        this.messageSentTotal = messageSentTotal;
    }

    public void setMessageErrorTotal(long messageErrorTotal) {
        this.messageErrorTotal = messageErrorTotal;
    }

    public void setBytesSentPerSecond(double bytesSentPerSecond) {
        this.bytesSentPerSecond = bytesSentPerSecond;
    }
}

