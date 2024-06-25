package com.redsun;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import com.azure.cosmos.util.CosmosPagedIterable;

import java.util.*;
import java.util.stream.Collectors;
import com.redsun.models.Item;

public class Facet {
    private static final String COSMOS_CONNECTION_STRING = "AccountEndpoint=https://roshancosmosdb.documents.azure.com:443/;AccountKey=xz79fmeCfF4nwG99XG30JHyAeWqVC93cJOgxtV4DKVKeiPutJhPOesdYlwUzCFnzavvl3SOuaGhwACDb7mm9Mg==;";
    private static final String COSMOS_KEY = "xz79fmeCfF4nwG99XG30JHyAeWqVC93cJOgxtV4DKVKeiPutJhPOesdYlwUzCFnzavvl3SOuaGhwACDb7mm9Mg==";
    private static final String DATABASE_ID = "SampleDB";
    private static final String CONTAINER_ID = "Users";

    CosmosClient cosmosClient = new CosmosClientBuilder()
                .endpoint(COSMOS_CONNECTION_STRING)
                .key(COSMOS_KEY)
                .buildClient();
  

    @FunctionName("factes")
    public HttpResponseMessage run(
            @HttpTrigger(name = "req", methods = {HttpMethod.GET}, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Java HTTP trigger processed a request.");
        

        

        CosmosContainer container = cosmosClient.getDatabase(DATABASE_ID).getContainer(CONTAINER_ID);

        String sqlQuery = "SELECT * FROM c";
        CosmosQueryRequestOptions queryOptions = new CosmosQueryRequestOptions();
        CosmosPagedIterable<Item> queryResults = container.queryItems(sqlQuery, queryOptions, Item.class);
        List<Item> allItems = queryResults.stream().collect(Collectors.toList());

        List<Item> qrgcategoryItems = allItems.stream().filter(item -> item.getPk().equals("qrgcategory")).collect(Collectors.toList());
        List<Item> endecadataItems = allItems.stream().filter(item -> item.getPk().equals("endecadata")).collect(Collectors.toList());

        List<Item> newItems = new ArrayList<>();
        for (Item endecaItem : endecadataItems) {
            String spec = endecaItem.getSpec();
            if (spec != null && !spec.isEmpty() && !Character.isLetter(spec.charAt(0)) && spec.length() < 4) {
                spec = String.format("%4s", spec).replace(' ', '0');
            }

            String base36Id = Integer.toString(Integer.parseInt(endecaItem.getId()), 36).toUpperCase();

            Item newItem = new Item();
            newItem.setPk("facets");
            newItem.setName(endecaItem.getDimensionName());
            final String finalSpec = spec; 
            newItem.setClassCode(finalSpec);
            newItem.setBase10(endecaItem.getId());
            newItem.setBase36(base36Id);
            newItem.setPriority("");
            newItem.setSeoTitle("");
            newItem.setMetaDescription("");
            newItem.setMetaKeywords("");

            Optional<Item> matchingQrgItem = qrgcategoryItems.stream()
                .filter(qrgItem -> Objects.equals(qrgItem.getDimvalSpec(), finalSpec))
                .findFirst();
            if (matchingQrgItem.isPresent()) {
                newItem.setDisplayName(matchingQrgItem.get().getDimvalDisplayName());
            }

            newItems.add(newItem);
        }

         List<List<Item>> batches = new ArrayList<>();
        int batchSize = 100;
        for (int i = 0; i < newItems.size(); i += batchSize) {
            batches.add(newItems.subList(i, Math.min(i + batchSize, newItems.size())));
        }

        for (List<Item> batch : batches) {
            List<CosmosItemOperation> operations = batch.stream()
                    .map(item -> CosmosBulkOperations.getCreateItemOperation(item, new PartitionKey(item.getPk())))
                    .collect(Collectors.toList());

            try {
                    CosmosBulkExecutionOptions bulkOptions = new CosmosBulkExecutionOptions();
                    Iterable<CosmosBulkOperationResponse<Object>> responses = container.executeBulkOperations(operations, bulkOptions);

                    for (CosmosBulkOperationResponse<Object> response : responses) {
                        CosmosBulkItemResponse itemResponse = response.getResponse();
                        if (itemResponse.getStatusCode() != 201) {
                            context.getLogger().warning("Failed to insert item: " + itemResponse.getStatusCode());
                        }
                    }
                } catch (CosmosException ex) {
                    context.getLogger().warning("Error during bulk insert: " + ex.getMessage());
                    return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error").build();
                }
        }

        cosmosClient.close();

        return request.createResponseBuilder(HttpStatus.OK).body("Data uploaded successfully").build();
    }

}
    
    
