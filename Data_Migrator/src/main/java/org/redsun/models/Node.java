package org.redsun.models;
import java.sql.Timestamp;

public class Node{
    public String parentSpec;
    public String displayName;
    public String dimensionName;
    public Timestamp lastUpdatedTime;

    public Node (String parentSpec, String displayName, String dimensionName, Timestamp lastUpdatedTime) {
        this.parentSpec = parentSpec;
        this.displayName = displayName;
        this.dimensionName = dimensionName;
        this.lastUpdatedTime = lastUpdatedTime;
    }
}