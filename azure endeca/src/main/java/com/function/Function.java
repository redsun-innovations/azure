package com.function;

import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;

import com.azure.cosmos.*;
import com.azure.cosmos.models.CosmosItemOperation;

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

public class Function {
    private static final String COSMOS_CONNECTION_STRING = "https://sasicosmodbaccount.documents.azure.com:443/";
    private static final String COSMOS_KEY = "gXdISAkvhZ2IBqOkwYsnfiBmoIB895YTXc6NA1bbn3PNMIRCvrQAF6KyEimjDFZw8ip6XQ5nNfDuACDbjXjCgw==";
    private static final String DATABASE_ID = "SampleDB";
    private static final String CONTAINER_ID = "Users";

    private static CosmosClient cosmosClient = new CosmosClientBuilder()
    .endpoint(COSMOS_CONNECTION_STRING)
    .key(COSMOS_KEY)
    .buildClient();


    @FunctionName("Uploadalldata")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        try {
            // Load and parse the JSON file
            InputStream inputStream = Function.class.getResourceAsStream("/facet.json");

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

            // Upload data in batches
            int batchSize = 100; 
            List<CosmosItemOperation> operations = new ArrayList<>();
            for (int i = 0; i < jsonData.size(); i++) {
                operations.add(CosmosBulkOperations.getCreateItemOperation(jsonData.get(i), new PartitionKey("/id")));

                if (operations.size() == batchSize || i == jsonData.size() - 1) {
                    // Execute bulk operation
                    CosmosBulkExecutionOptions bulkOptions = new CosmosBulkExecutionOptions();
                    Iterable<CosmosBulkOperationResponse<Object>> responses = container.executeBulkOperations(operations, bulkOptions);

                    for (CosmosBulkOperationResponse<Object> response : responses) {
                        int statusCode = response.getResponse().getStatusCode();
                        if (statusCode == 201) {
                            // Operation was successful
                        } else {
                            // Operation failed
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
}
