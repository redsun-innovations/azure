package com.redsun.Hierarchy_Api.configuration;

import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.CosmosDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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


    @Bean
    public CosmosClient cosmosClient() {
        return new CosmosClientBuilder()
                .endpoint(endpoint)
                .key(key)
                .gatewayMode()
                .buildClient();
    }
    @Bean
    public CosmosDatabase cosmosDatabase(CosmosClient cosmosClient) {
        return cosmosClient.getDatabase(databaseName);
    }

    @Bean
    public CosmosContainer cosmosContainer(CosmosDatabase cosmosDatabase) {
        return cosmosDatabase.getContainer(containerName);
    }
}
