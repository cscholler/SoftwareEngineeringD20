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

        public NodeEntry (Node n, Node p) {
            node = n;
            parent = p;
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

    public enum PathfindingMethod {
        Astar, BFS, DFS
    }


    private PathfindingMethod pathfindingMethod = PathfindingMethod.Astar;

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
            default:
            case Astar:
                return aStarPathFind(graph, source, destination);
            case BFS:
                return breadthFirstSearch(graph, source, destination);
            case DFS:
                return null;

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
                double twoDimensionalDistance = entry.node.getPosition().distance(destination.getPosition());

                entry.distFromDest = twoDimensionalDistance + Math.abs(entry.node.getFloor() - destination.getFloor()) * 100;
            }
        }

        // Start from the source Node and recursively update the priority Queue
        NodeEntry destNode = aStarPathFindHelper(destination);

        if (destNode != null)
            return Path.listToPath(entryToList(destNode));

        return null;
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

    @Override
    public PathfindingMethod getPathfindingMethod() {
        return pathfindingMethod;
    }

    @Override
    public void setPathfindingMethod(PathfindingMethod pathfindingMethod) {
        this.pathfindingMethod = pathfindingMethod;
    }

    /**
     * Finds a path between source and destination nodes using a breadth first search algorithm
     * @param graph the graph of nodes
     * @param source the starting node
     * @param destination the destination node
     * @return
     */
    private Path breadthFirstSearch(Graph graph, Node source, Node destination) {

        HashMap<Node, NodeEntry> nodes = new HashMap<>();

        List<Node> path = new ArrayList<>();
        LinkedList<Node> queue = new LinkedList<>();
        LinkedList<Node> visited = new LinkedList<>();

        for( Node n : graph.getNodes()) {
            nodes.put(n, new NodeEntry(n));
        }

        queue.add(source);
        boolean finished = false;
        Node parent = null;
        nodes.get(source).parent = parent;

        while (!queue.isEmpty() && !finished) {
            Node current = queue.removeFirst();

            if (destination == current) {
                finished = true;

                NodeEntry n = nodes.get(current);

                while (n.parent != null) {
                    path.add(n.node);

                    for (Node ne : visited) {
                        if (ne == n.parent) {
                            n = nodes.get(ne);
                            break;
                        }
                    }
                }

                path.add(n.node);

            }

            if (!visited.contains(current)) {
                visited.add(current);

                Collection<Node> neighbors = current.getNeighbors();

                if (!neighbors.isEmpty()) {
                    parent = current;
                    for (Node n : neighbors) {
                        if (!visited.contains(n)) {
                            queue.add(n);
                            nodes.get(n).parent = parent;
                        }
                    }
                }
            }
        }

        Collections.reverse(path);

        return Path.listToPath(path);
    }
}

