package io.lenses.topology.client;

public enum NodeType {
    STREAM,
    TOPIC,
    TABLE,
    SELECT,
    FILTER,
    JOIN,
    GROUPBY,
    AGGREGATE,
    COUNT,
    REPARTITION,
    MAP
}
