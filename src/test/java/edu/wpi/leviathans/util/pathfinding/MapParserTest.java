package edu.wpi.leviathans.util.pathfinding;

import edu.wpi.leviathans.util.pathfinding.graph.Graph;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

class MapParserTest {
    @Test
    public void parsingTest() {
        Graph testGraph =
                MapParser.parseMapToGraph(
                        new File(getClass().getClassLoader().getResource("edu/wpi/leviathans/util/pathfinding/MapBnodes.csv").getFile()),
                        new File(getClass().getResource("MapBedges.csv").getFile()));

        Assertions.assertEquals(2150, testGraph.getNode("BCONF00102").position.getX());
    }
}
