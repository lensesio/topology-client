package io.lenses.topology.client;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Topology {

    private final AppType appType;
    private final String appName;
    private String pid;
    private String machine;
    private final List<Node> nodes;

    Topology(AppType appType, String appName, List<Node> nodes) {
        this.appType = appType;
        this.appName = appName;
        this.nodes = nodes;
    }

    public String getAppName() {
        return appName;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public String getPid() {
        return pid;
    }

    public AppType getAppType() {
        return appType;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getMachine() {
        return machine;
    }

    public void setMachine(String machine) {
        this.machine = machine;
    }

    public List<String> getTopics() {
        return nodes.stream().filter(n -> n.getType() == NodeType.TOPIC).map(Node::getName).collect(Collectors.toList());
    }

    public String getDescription() {
        return appName + ":" + nodes.stream().filter(n -> n.getType() == NodeType.TOPIC).map(Node::getName).collect(Collectors.joining(","));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Topology topology = (Topology) o;
        return Objects.equals(appName, topology.appName) &&
                Objects.equals(nodes, topology.nodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(appName, nodes);
    }

    @Override
    public String toString() {
        return "Topology{" +
                "appName='" + appName + '\'' +
                ", nodes=" + nodes +
                '}';
    }

    public List<Node> nodesByType(NodeType type) {
        return nodes.stream().filter(node -> node.getType() == type).collect(Collectors.toList());
    }
}
