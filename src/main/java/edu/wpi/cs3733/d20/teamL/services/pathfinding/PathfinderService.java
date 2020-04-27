package edu.wpi.cs3733.d20.teamL.services.pathfinding;

import edu.wpi.cs3733.d20.teamL.entities.Graph;
import edu.wpi.cs3733.d20.teamL.entities.Node;
import edu.wpi.cs3733.d20.teamL.entities.Edge;
import edu.wpi.cs3733.d20.teamL.entities.Path;

import javax.inject.Inject;
import java.util.*;

public class PathfinderService implements IPathfinderService {

    class NodeEntry {
        public Node node;
        public Node parent;
        public double shortestPath = Integer.MAX_VALUE;
        public double distFromDest = 0;

        public NodeEntry(Node p_node) {
            node = p_node;
        }

        public NodeEntry(Node p_node, int p_shortestPath) {
            node = p_node;
            shortestPath = p_shortestPath;
        }

        public double absoluteDist() {
            if (Integer.MAX_VALUE - shortestPath < distFromDest) return Integer.MAX_VALUE;

            return shortestPath + distFromDest;
        }
    }

    private Comparator<NodeEntry> NodeEntrySorter = Comparator.comparing(NodeEntry::absoluteDist);
    private PriorityQueue<NodeEntry> priorityQueue = new PriorityQueue<>(NodeEntrySorter);
    private Map<Node, NodeEntry> priorityQueueKey = new HashMap<>();

    private boolean hasCoords = false;
    private enum PathfindingMethod {
        Astar, BFS, DFS
    };

    PathfindingMethod pathfindingMethod = PathfindingMethod.Astar;

    /**
     * Uses the a pathfinding algorithm to get a path between the source and destination node. Returns null
     * if there is no path
     *
     * @param source      The Node to start with
     * @param destination The Node to pathfind to
     * @return a list of Nodes in representing the path between source and destination (inclusive).
     */
    public Path pathfind(Graph graph, Node source, Node destination) {
        switch (pathfindingMethod) {
            case Astar:
                return aStarPathFind(graph, source, destination);
            case BFS:
                return null;
            case DFS:
                return depthFirstFind(graph, source, destination);
            default:
                return aStarPathFind(graph, source, destination);
        }
    }

    /**
     * Uses the A-Star algorithm to get a path between the source and destination node. Returns null
     * if there is no path
     *
     * @param source      The Node to start with
     * @param destination The Node to pathfind to
     * @return a list of Nodes in representing the path between source and destination (inclusive).
     */
    private Path aStarPathFind(Graph graph, Node source, Node destination) {

        // Initialize all nodes in the priority queue to infinity but don't add the source
        for (Node node : graph.getNodes()) {
            addNode(node);
        }

        setShortestPath(priorityQueueKey.get(source), 0);

        // Calculate the distance of each node from the destination if coordinates are in the data
        if (hasCoords) {
            for (NodeEntry entry : priorityQueue) {
                double x1 = entry.node.getPosition().getX();
                double y1 = entry.node.getPosition().getY();
                double x2 = destination.getPosition().getX();
                double y2 = destination.getPosition().getY();

                int distance = (int) Math.round(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));

                entry.distFromDest = distance;
            }
        }

        // Start from the source Node and recursively update the priority Queue
        NodeEntry destNode = aStarPathFindHelper(destination);

        return Path.listToPath(entryToList(destNode));
    }

    private NodeEntry aStarPathFindHelper(Node destination) {

        while (!priorityQueue.isEmpty()) {
            NodeEntry currentNode = priorityQueue.poll();

            if (currentNode.node.equals(destination)) {
                return currentNode;
            }

            for (Edge edge : currentNode.node.getEdges()) {
                Node otherNode = edge.getDestination();

                // Set otherNode NodeEntry shortestPath to currentNode.shortestPath + edge.length
                NodeEntry otherNodeEntry = priorityQueueKey.get(otherNode);
                if (currentNode.shortestPath + edge.getLength() < otherNodeEntry.shortestPath) {
                    setShortestPath(otherNodeEntry, currentNode.shortestPath + edge.getLength());
                    otherNodeEntry.parent = currentNode.node;
                }
            }
        }

        // Return null if there is no path between source and destination.
        return null;
    }

    /**
     * Performs Depth First Traversal
     *
     * @param source The starting Node
     * @param destination The ending Node
     * @return
     */
    public Path depthFirstFind(Graph graph, Node source, Node destination) {

        //List<Node> visitedNodes = new LinkedList<>();
        Collection<Node> neighbors = source.getNeighbors();
        int size = neighbors.size();
        Vector<Boolean> visitedNodes = new Vector<Boolean>(size);

        //Initialize everything to null
        for (int i = 0; i < size; i++) {
            visitedNodes.add(false);
        }
        //Iterate through the neighbors of the source Node
        for (int i = 0; i < neighbors.size(); i++) {

            //Checks to see if the neighbors are null (not visited)
            if (!visitedNodes.get(i)) {

                depthFirstHelper(i, visitedNodes);
            }
        }

        return Path.listToPath(entryToList(destination));
    }

    /**
     * A recursive call to get all the Nodes visited
     *
     * @param index The index of the Node to get
     * @param visited Keeps track of which Nodes were visited
     */
    private void depthFirstHelper(int index, Vector<Boolean> visited) {

        Stack<Integer> nodeStack = new Stack<>();
        //Pushes the node into the stack
        nodeStack.push(index);
        //Checks if the stack is empty
        while(nodeStack.empty() == false) {

            index = nodeStack.peek();
            nodeStack.pop();

            if(visited.get(index) == false) {

                visited.set(index, true);
            }
        }
    }

    /**
     * Converts a NodeEntry's chain of parents into a list representing the path
     *
     * @param entry
     * @return
     */
    private LinkedList<Node> entryToList(NodeEntry entry) {
        LinkedList<Node> path = new LinkedList<Node>();
        NodeEntry current = entry;
        while (current.shortestPath != 0) {
            path.addFirst(current.node);
            current = priorityQueueKey.get(current.parent);
        }
        path.addFirst(current.node);
        return path;
    }

    private void addNode(Node node) {
        NodeEntry newEntry = new NodeEntry(node);
        priorityQueue.add(newEntry);
        priorityQueueKey.put(node, newEntry);

        if (node.getPosition() != null) {
            if (priorityQueue.size() == 1) hasCoords = true;
        } else {
            hasCoords = false;
        }
    }

    private void removeNode(Node node) {
        priorityQueue.remove(priorityQueueKey.get(node));
        priorityQueueKey.remove(node);
    }

    private void setShortestPath(NodeEntry nodeEntry, double newShortestPath) {
        priorityQueue.remove(nodeEntry);
        nodeEntry.shortestPath = newShortestPath;
        priorityQueue.add(nodeEntry);
    }
}
