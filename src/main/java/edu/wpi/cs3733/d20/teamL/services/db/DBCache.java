package edu.wpi.cs3733.d20.teamL.services.db;

import java.sql.ResultSet;
import java.util.ArrayList;

public class DBCache {
    private static DBCache cache;

    public DBCache() {
        cacheNodes();
        cacheEdges();
    }

    //Tables that can be cached
    private ArrayList<ArrayList<String>> nodeCache = new ArrayList<>();
    private ArrayList<ArrayList<String>> edgeCache = new ArrayList<>();

    DatabaseService db = new DatabaseService(); //this should be changes eventually


    /**
     * cacheNode: Populates the node cache with nodes from the Database
     */
    public void cacheNodes() {
        ResultSet resSet = db.executeQuery(DBConstants.selectAllNodes);
        clearEdgeCache();
        nodeCache = db.getTableFromResultSet(resSet);
    }

    /**
     * clearNodeCache: Clears the Nodes cache
     */
    public void clearNodeCache() {
        nodeCache.clear();
    }

    public ArrayList<ArrayList<String>> getNodeCache() {
        return nodeCache;
    }

    /**
     * cacheEdges: Populates the edge cache with edges from the Database
     */
    public void cacheEdges() {
        ResultSet resSet = db.executeQuery(DBConstants.selectAllEdges);
        clearEdgeCache();
        edgeCache = db.getTableFromResultSet(resSet);
    }

    /**
     * clearEdgeCache: Clears the Edges cache
     */
    public void clearEdgeCache() {
        edgeCache.clear();
    }
}
