package com.redsun.api.hierarchy.configuration;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for CosmosDB connectivity.
 */
@Configuration
public class CosmosDbConfiguration {

    @Value("${azure.cosmos.endpoint}")
    private String endpoint;

    @Value("${azure.cosmos.key}")
    private String key;

    @Value("${azure.cosmos.database}")
    private String databaseName;

    @Value("${azure.cosmos.container}")
    private String containerName;



    /**
     * Bean definition for creating a CosmosClient instance.
     *
     * @return CosmosClient instance configured with endpoint and key.
     */

    @Bean
    public CosmosClient cosmosClient() {
        return new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .gatewayMode()
                .buildClient();
    }

    /**
     * Bean definition for obtaining a CosmosDatabase instance.
     *
     * @param cosmosClient The CosmosClient instance to use for accessing the database.
     * @return CosmosDatabase instance corresponding to the configured database name.
     */
    @Bean
    public CosmosDatabase cosmosDatabase(CosmosClient cosmosClient) {
        return cosmosClient.getDatabase(databaseName);
    }

    /**
     * Bean definition for obtaining a CosmosContainer instance.
     *
     * @param cosmosDatabase The CosmosDatabase instance from which to obtain the container.
     * @return CosmosContainer instance corresponding to the configured container name.
     */
    @Bean
    public CosmosContainer cosmosContainer(CosmosDatabase cosmosDatabase) {
        return cosmosDatabase.getContainer(containerName);
    }
}
