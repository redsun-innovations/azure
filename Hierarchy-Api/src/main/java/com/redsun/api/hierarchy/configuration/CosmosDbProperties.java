package com.redsun.api.hierarchy.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "cosmosdb")
public class CosmosDbProperties {
    private String endpoint;
    private String key;
    private String databaseName;
    private String containerName;
    private String connectionMode;


    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getContainerName() {
        return containerName;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    public String getConnectionMode() {
        return connectionMode;
    }

    public void setConnectionMode(String connectionMode) {
        this.connectionMode = connectionMode;
    }
}
