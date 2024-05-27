// package com.function;

// import com.microsoft.azure.functions.ExecutionContext;
// import com.microsoft.azure.functions.HttpMethod;
// import com.microsoft.azure.functions.HttpRequestMessage;
// import com.microsoft.azure.functions.HttpResponseMessage;
// import com.microsoft.azure.functions.HttpStatus;
// import com.microsoft.azure.functions.annotation.AuthorizationLevel;
// import com.microsoft.azure.functions.annotation.FunctionName;
// import com.microsoft.azure.functions.annotation.HttpTrigger;

// import java.util.Optional;

// /**
//  * Azure Functions with HTTP Trigger.
//  */
// public class Function {
//     /**
//      * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
//      * 1. curl -d "HTTP Body" {your host}/api/HttpExample
//      * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
//      */
//     @FunctionName("HttpExample")
//     public HttpResponseMessage run(
//             @HttpTrigger(
//                 name = "req",
//                 methods = {HttpMethod.GET, HttpMethod.POST},
//                 authLevel = AuthorizationLevel.ANONYMOUS)
//                 HttpRequestMessage<Optional<String>> request,
//             final ExecutionContext context) {
//         context.getLogger().info("Java HTTP trigger processed a request.");

//         // Parse query parameter
//         final String query = request.getQueryParameters().get("name");
//         final String name = request.getBody().orElse(query);

//         if (name == null) {
//             return request.createResponseBuilder(HttpStatus.BAD_REQUEST).body("Please pass a name on the query string or in the request body").build();
//         } else {
//             return request.createResponseBuilder(HttpStatus.OK).body("Hello, " + name).build();
//         }
//     }
// }


package com.function;

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

public class Function {
    private static final String COSMOS_CONNECTION_STRING = "https://roshancosmosdb.documents.azure.com:443/";
    private static final String COSMOS_KEY = "glpEB8ooa7l0ovOAmovVPbB6yD9htroSL1uDiGOCzuEXDHnAou4XQdZTfyGj7kmOLcJ4bq29ybmOACDbIvycZQ==";
    private static final String DATABASE_ID = "SampleDB";
    private static final String CONTAINER_ID = "Users";

    CosmosClient cosmosClient = new CosmosClientBuilder()
                .endpoint(COSMOS_CONNECTION_STRING)
                .key(COSMOS_KEY)
                .buildClient();
  

    @FunctionName("Factes")
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
            newItem.setClassCode(spec);
            newItem.setBase10(endecaItem.getId());
            newItem.setBase36(base36Id);
            newItem.setPriority("");
            newItem.setSeoTitle("");
            newItem.setMetaDescription("");
            newItem.setMetaKeywords("");

            Optional<Item> matchingQrgItem = qrgcategoryItems.stream().filter(qrgItem -> Objects.equals(qrgItem.getDimvalSpec(), spec)).findFirst();
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


    
    public static class Item {
        private String pk;
        private String name;
        private String classCode;
        private String base10;
        private String base36;
        private String priority;
        private String seoTitle;
        private String metaDescription;
        private String metaKeywords;
        private String displayName;
        private String spec;
        private String id;
        private String dimensionName;
        private String dimvalSpec;
        private String dimvalDisplayName;

        // Getters and setters
        public String getPk() {
            return pk;
        }

        public void setPk(String pk) {
            this.pk = pk;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getClassCode() {
            return classCode;
        }

        public void setClassCode(String classCode) {
            this.classCode = classCode;
        }

        public String getBase10() {
            return base10;
        }

        public void setBase10(String base10) {
            this.base10 = base10;
        }

        public String getBase36() {
            return base36;
        }

        public void setBase36(String base36) {
            this.base36 = base36;
        }

        public String getPriority() {
            return priority;
        }

        public void setPriority(String priority) {
            this.priority = priority;
        }

        public String getSeoTitle() {
            return seoTitle;
        }

        public void setSeoTitle(String seoTitle) {
            this.seoTitle = seoTitle;
        }

        public String getMetaDescription() {
            return metaDescription;
        }

        public void setMetaDescription(String metaDescription) {
            this.metaDescription = metaDescription;
        }

        public String getMetaKeywords() {
            return metaKeywords;
        }

        public void setMetaKeywords(String metaKeywords) {
            this.metaKeywords = metaKeywords;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getSpec() {
            return spec;
        }

        public void setSpec(String spec) {
            this.spec = spec;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDimensionName() {
            return dimensionName;
        }

        public void setDimensionName(String dimensionName) {
            this.dimensionName = dimensionName;
        }

        public String getDimvalSpec() {
            return dimvalSpec;
        }

        public void setDimvalSpec(String dimvalSpec) {
            this.dimvalSpec = dimvalSpec;
        }

        public String getDimvalDisplayName() {
            return dimvalDisplayName;
        }

        public void setDimvalDisplayName(String dimvalDisplayName) {
            this.dimvalDisplayName = dimvalDisplayName;
        }
    }
}