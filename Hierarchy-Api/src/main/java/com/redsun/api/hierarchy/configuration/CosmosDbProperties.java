package com.redsun.api.hierarchy.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for CosmosDB connection settings.
 */
@Component
@ConfigurationProperties(prefix = "cosmosdb")
public class CosmosDbProperties {
    private String endpoint;
    private String key;
    private String databaseName;
    private String containerName;
    private String connectionMode;


    /**
     * Get the CosmosDB endpoint URL.
     *
     * @return The endpoint URL of the CosmosDB instance.
     */
    public String getEndpoint() {
        return endpoint;
    }

    /**
     * Set the CosmosDB endpoint URL.
     *
     * @param endpoint The endpoint URL to set.
     */
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Get the CosmosDB access key.
     *
     * @return The access key for CosmosDB.
     */
    public String getKey() {
        return key;
    }

    /**
     * Set the CosmosDB access key.
     *
     * @param key The access key to set.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Get the name of the CosmosDB database.
     *
     * @return The name of the CosmosDB database.
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Set the name of the CosmosDB database.
     *
     * @param databaseName The name of the database to set.
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Get the name of the CosmosDB container.
     *
     * @return The name of the CosmosDB container.
     */
    public String getContainerName() {
        return containerName;
    }

    /**
     * Set the name of the CosmosDB container.
     *
     * @param containerName The name of the container to set.
     */
    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }

    /**
     * Get the connection mode for CosmosDB.
     *
     * @return The connection mode used for CosmosDB connectivity.
     */
    public String getConnectionMode() {
        return connectionMode;
    }

    /**
     * Set the connection mode for CosmosDB.
     *
     * @param connectionMode The connection mode to set.
     */
    public void setConnectionMode(String connectionMode) {
        this.connectionMode = connectionMode;
    }
}
