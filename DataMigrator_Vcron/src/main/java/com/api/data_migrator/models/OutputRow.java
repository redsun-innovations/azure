package com.api.data_migrator.models;

import java.sql.Timestamp;

public class OutputRow {
    String uuid;
    public String spec;
    public String displayName;
    String dimensionName;
    String parentSpec;
    public String path;
    public int depth;
    Timestamp lastUpdatedTime;

    public OutputRow(String uuid, String spec, String displayName, String dimensionName, String parentSpec, String path, int depth, Timestamp lastUpdatedTime) {
        this.uuid = uuid;
        this.spec = spec;
        this.displayName = displayName;
        this.dimensionName = dimensionName;
        this.parentSpec = parentSpec;
        this.path = path;
        this.depth = depth;
        this.lastUpdatedTime = lastUpdatedTime;
    }
}