package io.lenses.topology.client;

import java.util.List;
import java.util.Objects;

public class Node {

    private final String name;
    private final String description;
    private final NodeType type;
    private final Representation representation;
    private final DecoderType keyType;
    private final DecoderType valueType;
    private final List<String> parents;

    public Node(String name, String description, NodeType type, DecoderType keyType, DecoderType valueType, Representation representation, List<String> parents) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.keyType = keyType;
        this.valueType = valueType;
        this.representation = representation;
        this.parents = parents;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public DecoderType getKeyType() {
        return keyType;
    }

    public DecoderType getValueType() {
        return valueType;
    }

    public Representation getRepresentation() {
        return representation;
    }

    public List<String> getParents() {
        return parents;
    }

    public NodeType getType() {
        return type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return Objects.equals(name, node.name) &&
                Objects.equals(description, node.description) &&
                type == node.type &&
                keyType == node.keyType &&
                valueType == node.valueType &&
                representation == node.representation &&
                Objects.equals(parents, node.parents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, type, keyType, valueType, representation, parents);
    }

    @Override
    public String toString() {
        return "Node{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", representation=" + representation +
                ", keyType=" + keyType +
                ", valueType=" + valueType +
                ", parents=" + parents +
                '}';
    }
}
