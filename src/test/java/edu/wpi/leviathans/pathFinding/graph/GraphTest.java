package edu.wpi.leviathans.pathFinding.graph;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assert.*;
import org.junit.jupiter.api.Test;

class GraphTest {
  @Test
  public void createGraph() throws Exception {
    Graph newGraph = new Graph();
    newGraph.addNode(new Node("Test_Node"));

    assertEquals(newGraph.getNode("Test_Node").getName(), "Test_Node");
  }
}
