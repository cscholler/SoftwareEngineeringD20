package edu.wpi.leviathans.pathFinding;

import static org.junit.jupiter.api.Assertions.*;

import edu.wpi.leviathans.pathFinding.graph.Graph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MapParserTest {
  @Test
  public void parsingTest() {
    Graph testGraph =
        MapParser.parseMapToGraph(
            "C:\\Users\\chjm6\\Downloads\\Faulkner Hospital Data\\MapBnodes.csv",
            "C:\\Users\\chjm6\\Downloads\\Faulkner Hospital Data\\MapBedges.csv");

    Assertions.assertEquals(2150, testGraph.getNode("BCONF00102").data.get(MapParser.X_LABEL));
  }
}
