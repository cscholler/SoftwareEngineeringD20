package edu.wpi.cs3733.d20.teamL.services.pathfinding;

import edu.wpi.cs3733.d20.teamL.entities.Graph;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.entities.Path;

public interface IPathfinderService {
	Path pathfind(Graph graph, Node source, Node destination);

    PathfinderService.PathfindingMethod getPathfindingMethod();

	void setPathfindingMethod(PathfinderService.PathfindingMethod pathfindingMethod);

	boolean isHandicapped();

	void setHandicapped(boolean handicapped);
}
