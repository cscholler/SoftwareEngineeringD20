package edu.wpi.leviathans.pathFinding;

import edu.wpi.leviathans.pathFinding.graph.*;
import java.util.List;

public interface Algorithms {

  /**
   * Uses the A-Star algorithm to get a path between the source and destination node. Throws error
   * if there is no path.
   *
   * @param source The Node to start with
   * @param destination The Node to pathfind to
   * @return a list of Nodes in representing the path between source and destination (inclusive).
   */
  static List<Node> aStarPathFind(Node source, Node destination) {
    // TODO: Implement
    // Should throw an error if there is no path between source and destination.
    return null;
  }
}
