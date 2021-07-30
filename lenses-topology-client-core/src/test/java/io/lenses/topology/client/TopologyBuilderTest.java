package io.lenses.topology.client;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TopologyBuilderTest {
    @Test
    public void testTopologyBuilder() {
        Topology actual = TopologyBuilder.start(AppType.KafkaStreams, "sammyapp")
                .withNode("node1", NodeType.TABLE).withDescription("my node").withKeyType(DecoderType.INT).withValueType(DecoderType.JSON).asTable().endNode()
                .withNode("node2", NodeType.AGGREGATE).withDescription("super node").withValueType(DecoderType.STRING).withParent("node1").asStream().endNode()
                .build();

        Topology expected = new Topology(
                AppType.KafkaStreams,
                "sammyapp",
                Arrays.asList(
                        new Node("node1", "my node", NodeType.TABLE, DecoderType.INT, DecoderType.JSON, Representation.TABLE, emptyList()),
                        new Node("node2", "super node", NodeType.AGGREGATE, null, DecoderType.STRING, Representation.STREAM, Arrays.asList("node1"))
                )
        );

        assertEquals(actual, expected);
    }
}
