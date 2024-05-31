package com.redsun;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosItemOperation;
import com.azure.cosmos.models.CosmosItemRequestOptions;
import com.azure.cosmos.models.CosmosItemResponse;
import com.azure.cosmos.models.CosmosBulkOperations;
import com.azure.cosmos.models.CosmosBulkExecutionOptions;

import com.azure.cosmos.models.CosmosBulkOperationResponse;
import com.azure.cosmos.models.PartitionKey;


import org.apache.commons.io.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Endeca {
    private static final String COSMOS_CONNECTION_STRING = "AccountEndpoint=https://roshancosmosdb.documents.azure.com:443/;AccountKey=xz79fmeCfF4nwG99XG30JHyAeWqVC93cJOgxtV4DKVKeiPutJhPOesdYlwUzCFnzavvl3SOuaGhwACDb7mm9Mg==;";
    private static final String COSMOS_KEY = "xz79fmeCfF4nwG99XG30JHyAeWqVC93cJOgxtV4DKVKeiPutJhPOesdYlwUzCFnzavvl3SOuaGhwACDb7mm9Mg==";
    private static final String DATABASE_ID = "SampleDB";
    private static final String CONTAINER_ID = "Users";

    private static CosmosClient cosmosClient = new CosmosClientBuilder()
    .endpoint(COSMOS_CONNECTION_STRING)
    .key(COSMOS_KEY)
    .buildClient();


    @FunctionName("Endeca-data")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        try {
           
            InputStream inputStream = Endeca.class.getResourceAsStream("/QRG.json");

            if (inputStream == null) {
                context.getLogger().severe("Failed to load JSON file.");
                return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Internal Server Error")
                        .build();
            }
            
            context.getLogger().info("JSON file loaded successfully.");

           
            String fileContent;
            try {
                fileContent = IOUtils.toString(inputStream, "UTF-8");
            } catch (IOException e) {
                context.getLogger().severe("An error occurred while reading the JSON file: " + e.getMessage());
                return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Internal Server Error: Error reading JSON file")
                        .build();
            } finally {
                IOUtils.closeQuietly(inputStream); 
            }

            ObjectMapper objectMapper = new ObjectMapper();
            List<Object> jsonData;
            try {
                jsonData = objectMapper.readValue(fileContent, new TypeReference<List<Object>>() {});
            } catch (JsonProcessingException e) {
                context.getLogger().severe("An error occurred while parsing JSON data: " + e.getMessage());
                return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Internal Server Error: Error parsing JSON data")
                        .build();
            }

            CosmosContainer container = cosmosClient.getDatabase(DATABASE_ID).getContainer(CONTAINER_ID);

            
                int batchSize = 100; 
                List<CosmosItemOperation> operations = new ArrayList<>();
                for (int i = 0; i < jsonData.size(); i++) {
                    Object item = jsonData.get(i);

                    context.getLogger().info("Processing item: " + item.toString());

                    operations.add(CosmosBulkOperations.getCreateItemOperation(item, new PartitionKey("endecadata")));

                    if (operations.size() == batchSize || i == jsonData.size() - 1) {
                    
                        CosmosBulkExecutionOptions bulkOptions = new CosmosBulkExecutionOptions();
                        Iterable<CosmosBulkOperationResponse<Object>> responses = container.executeBulkOperations(operations, bulkOptions);

                        for (CosmosBulkOperationResponse<Object> response : responses) {
                            int statusCode = response.getResponse().getStatusCode();
                            if (statusCode == 201) {

                                context.getLogger().info("Successfully inserted item with status code: " + statusCode);
                            } else {
                                
                                context.getLogger().warning("Failed to insert item. Status code: " + statusCode);
                            }
                        }
                        operations.clear();
                    }
            }

            return request.createResponseBuilder(HttpStatus.OK)
                .body("Data uploaded successfully")
                .header("Content-Type", "text/plain")
                .build();
        } catch (Exception e) {
            context.getLogger().severe("An exception occurred: " + e.getMessage());
            e.printStackTrace();
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error")
                    .build();
        }
    }
    
    @FunctionName("deleteItemsByPartitionKey")
    public HttpResponseMessage deleteItems(
            @HttpTrigger(name = "req", methods = {HttpMethod.DELETE}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("HTTP trigger to delete items processed a request.");

        String partitionKey = request.getQueryParameters().get("partitionKey");
        if (partitionKey == null) {
            return request.createResponseBuilder(HttpStatus.BAD_REQUEST)
                    .body("Please pass a partitionKey on the query string")
                    .build();
        }

        try {
            CosmosContainer container = cosmosClient.getDatabase(DATABASE_ID).getContainer(CONTAINER_ID);

            CosmosItemResponse<?> deleteResponse = container.deleteAllItemsByPartitionKey(
                    new PartitionKey(partitionKey), new CosmosItemRequestOptions()).block();

            return request.createResponseBuilder(HttpStatus.OK)
                    .body("Items deleted successfully")
                    .header("Content-Type", "text/plain")
                    .build();
        } catch (Exception e) {
            context.getLogger().severe("Error: " + e.getMessage());
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Internal Server Error")
                    .build();
        }
    }
}
