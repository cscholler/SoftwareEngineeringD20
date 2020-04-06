package edu.wpi.leviathans.pathFinding;

import edu.wpi.leviathans.pathFinding.graph.*;

import java.util.*;

public class PathFinder {

    class NodeEntry {
        public Node node;
        public Node parent;
        public int shortestPath = Integer.MAX_VALUE;
        public int distFromDest = 0;

        public NodeEntry(Node p_node) {
            node = p_node;
        }

        public NodeEntry(Node p_node, int p_shortestPath) {
            node = p_node;
            shortestPath = p_shortestPath;
        }

        public int absoluteDist() {
            if (Integer.MAX_VALUE - shortestPath < distFromDest) return Integer.MAX_VALUE;

            return shortestPath + distFromDest;
        }
    }

    private Comparator<NodeEntry> NodeEntrySorter = Comparator.comparing(NodeEntry::absoluteDist);
    private PriorityQueue<NodeEntry> priorityQueue = new PriorityQueue<NodeEntry>(NodeEntrySorter);
    private Map<Node, NodeEntry> priorityQueueKey = new HashMap<Node, NodeEntry>();

    private boolean hasCoords = false;

    /**
     * Uses the A-Star algorithm to get a path between the source and destination node. Returns null if there is no path
     *
     * @param source      The Node to start with
     * @param destination The Node to pathfind to
     * @return a list of Nodes in representing the path between source and destination (inclusive).
     */
    public static Path aStarPathFind(Graph graph, Node source, Node destination) {
        PathFinder pathFinder = new PathFinder();
        return pathFinder.doAStarPathFind(graph, source, destination);
    }

    /**
     * Uses the A-Star algorithm to get a path between the source and destination node. Returns null if there is no path
     *
     * @param source      The Node to start with
     * @param destination The Node to pathfind to
     * @return a list of Nodes in representing the path between source and destination (inclusive).
     */
    public Path doAStarPathFind(Graph graph, Node source, Node destination) {

        // Initialize all nodes in the priority queue to infinity but don't add the source
        for (Node node : graph.getNodes()) {
            addNode(node);
        }

        priorityQueueKey.get(source).shortestPath = 0;

        // Calculate the distance of each node from the destination if coordinates are in the data
        if(hasCoords) {
            for (NodeEntry entry : priorityQueue) {
                int x1 = (int) entry.node.data.get(MapParser.DATA_LABELS.X);
                int y1 = (int) entry.node.data.get(MapParser.DATA_LABELS.Y);
                int x2 = (int) destination.data.get(MapParser.DATA_LABELS.X);
                int y2 = (int) destination.data.get(MapParser.DATA_LABELS.Y);

                int distance = (int) Math.round(Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)));

                entry.distFromDest = distance;
            }
        }

        // Start from the source Node and recursively update the priority Queue
        NodeEntry destNode = aStarPathFindHelper(source, destination);

        return Path.listToPath(entryToList(destNode));
    }

    private NodeEntry aStarPathFindHelper(Node source, Node destination) {

        while (!priorityQueue.isEmpty()) {
            NodeEntry currentNode = priorityQueue.poll();

            if (currentNode.node.equals(destination)) {
                return currentNode;
            }

            for (Edge edge : currentNode.node.getEdges()) {
                Node otherNode = edge.destination;

                // Set otherNode NodeEntry shortestPath to currentNode.shortestPath + edge.length
                NodeEntry otherNodeEntry = priorityQueueKey.get(otherNode);
                if (currentNode.shortestPath + edge.length < otherNodeEntry.shortestPath) {
                    otherNodeEntry.shortestPath = currentNode.shortestPath + edge.length;
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
            System.out.println(current.node.getName());
            current = priorityQueueKey.get(current.parent);
        }
        System.out.println(current.node.getName());
        path.addFirst(current.node);
        return path;
    }

    private void addNode(Node node) {
        NodeEntry newEntry = new NodeEntry(node);
        priorityQueue.add(newEntry);
        priorityQueueKey.put(node, newEntry);

        if(node.data.containsKey(MapParser.DATA_LABELS.X) && node.data.containsKey(MapParser.DATA_LABELS.X)) {
            if(priorityQueue.size() == 1) hasCoords = true;
        } else {
            hasCoords = false;
        }
    }

    private void removeNode(Node node) {
        priorityQueue.remove(priorityQueueKey.get(node));
        priorityQueueKey.remove(node);
    }
}
