package edu.wpi.cs3733.d20.teamL.util.io;

import edu.wpi.cs3733.d20.teamL.services.db.DBConstants;
import edu.wpi.cs3733.d20.teamL.services.db.DatabaseService;

import java.sql.ResultSet;
import java.util.ArrayList;

public class DBCache {
    private static DBCache cache;
    private DBCache(){}
    private ArrayList<ArrayList<String>> nodeCache;
    DatabaseService db = new DatabaseService();

    /**
     * cacheNode: Populates the node cache with nodes from the Database
     *
     */
    public void cacheNode() {
        ResultSet resSet = db.executeQuery(DBConstants.selectAllNodes);
        nodeCache = db.getTableFromResultSet(resSet);

    }

    /**
     * clearNodeCache: Clears the cache
     *
     */
    public void clearNodeCache() {
        nodeCache.clear();
    }

    public ArrayList<ArrayList<String>> getNodeCache(){
        return nodeCache;
    }
    private static class singletonHelper{
        private static final DBCache cache = new DBCache();
    }

    public static DBCache getCache(){
        return singletonHelper.cache;
    }
}
