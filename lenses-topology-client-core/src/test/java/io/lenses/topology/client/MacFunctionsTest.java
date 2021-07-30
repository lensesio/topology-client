package io.lenses.topology.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MacFunctionsTest {
    @Test
    void getMacShouldReturnNull() {
        assertNotNull(TopologyClient.getMac());
    }
}
