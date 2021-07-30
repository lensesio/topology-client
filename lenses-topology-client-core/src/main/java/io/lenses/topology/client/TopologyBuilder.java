package io.lenses.topology.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TopologyBuilder {

    private final AppType appType;
    private final String appName;
    private final List<Node> nodes = new ArrayList<>();

    private TopologyBuilder(AppType appType, String appName) {
        this.appType = appType;
        this.appName = appName;
    }

    public static TopologyBuilder start(String appName) {
        return start(AppType.Other, appName);
    }

    public static TopologyBuilder start(AppType type, String appName) {
        return new TopologyBuilder(type, appName);
    }

    public NodeBuilder withNode(String name, NodeType type) {
        return new NodeBuilder(this, type, name);
    }

    public NodeBuilder withTopic(String name) {
        return withNode(name, NodeType.TOPIC);
    }

    public Topology build() {
        if (nodes.isEmpty())
            throw new IllegalStateException("Must define at least one node");
        return new Topology(appType, appName, nodes);
    }

    public class NodeBuilder {

        private final TopologyBuilder builder;
        private final NodeType type;
        private final String name;
        private String description;
        private final List<String> parents = new ArrayList<>();
        private DecoderType keyType = null;
        private DecoderType valueType = null;
        private Representation representation = null;

        NodeBuilder(TopologyBuilder builder, NodeType type, String name) {
            this.builder = builder;
            this.type = type;
            this.name = name;
        }

        public NodeBuilder withKeyType(DecoderType type) {
            keyType = type;
            return this;
        }

        public NodeBuilder withValueType(DecoderType type) {
            valueType = type;
            return this;
        }

        public NodeBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public NodeBuilder asTable() {
            this.representation = Representation.TABLE;
            return this;
        }

        public NodeBuilder asStream() {
            this.representation = Representation.STREAM;
            return this;
        }

        public NodeBuilder withRepresentation(Representation representation) {
            this.representation = representation;
            return this;
        }

        public NodeBuilder withParents(String... parents) {
            return withParents(Arrays.asList(parents));
        }

        public NodeBuilder withParents(List<String> parents) {
            this.parents.addAll(parents);
            return this;
        }

        public NodeBuilder withParent(String parent) {
            this.parents.add(parent);
            return this;
        }

        public TopologyBuilder endNode() {
            if (representation == null)
                throw new IllegalStateException("Representation must be set by invoking asTable or asStream on the node builder");
            builder.nodes.add(new Node(name, description, type, keyType, valueType, representation, parents));
            return builder;
        }
    }
}

