package com.redsun.api.hierarchy.repository;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redsun.api.hierarchy.constant.Const;
import com.redsun.api.hierarchy.model.HierarchyEntity;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Repository for managing and querying hierarchy data in Azure Cosmos DB.
 * This repository provides methods to fetch hierarchy data, including class codes and hierarchical relationships.
 */
@Repository
public class CosmosDbHierarchyRepository implements HierarchyRepository {
    private final CosmosContainer container;
    private final ObjectMapper objectMapper;
    private final CosmosHierarchyRepositoryExtends repository;

    /**
     * Constructs a new instance of {@code CosmosDbHierarchyRepository} with the specified {@code CosmosContainer}.
     *
     * @param container the CosmosContainer instance to be used for querying the database
     */

    public CosmosDbHierarchyRepository(CosmosContainer container, CosmosHierarchyRepositoryExtends repository) {
        this.container = container;
        this.objectMapper = new ObjectMapper();

        this.repository = repository;
    }

    /**
     * Fetches hierarchy data for the specified class code.
     *
     * @param classCode the class code for which to fetch hierarchy data
     * @return a list of maps representing the hierarchy data
     */


    public List<Map<String, Object>> fetchClassCodeData(String classCode) {
//        String query = "SELECT c.displayName, c.base36Id, c.path, c.classCode FROM c WHERE c.pk = 'hierarchy' ORDER BY c.path ASC";

        List<HierarchyEntity> query = repository.fetchByClassCodeData(classCode);
        CosmosPagedIterable<JsonNode> queryResults = container.queryItems(query.toString(), new CosmosQueryRequestOptions(), JsonNode.class);

        List<Map<String, Object>> items = new ArrayList<>();

        for (JsonNode item : queryResults) {
            Map<String, Object> itemMap = objectMapper.convertValue(item, new TypeReference<Map<String, Object>>() {});
            items.add(itemMap);
        }


        Map<String, Map<String, Object>> classCodeToHierarchy = new HashMap<>();
        Map<String, String> displayNameToBase36Id = mapDisplayNameToBase36Id(items);

        boolean classCodeFound = items.stream()
                .anyMatch(item -> classCode.equals(item.get(Const.CLASSCODE)));

        List<Map<String, Object>> response = new ArrayList<>();

        if (!classCodeFound) {
            response.add(placeholderEntry(classCode));
        }

        processItemsForHierarchy(items, classCode, displayNameToBase36Id, classCodeToHierarchy);

        response.addAll(classCodeToHierarchy.values());

        return response;
    }
    /**
     * Maps display names to base36 IDs from the given list of items.
     *
     * @param items the list of items containing display names and base36 IDs
     * @return a map with display names as keys and base36 IDs as values
     */


    private Map<String, String> mapDisplayNameToBase36Id(List<Map<String, Object>> items) {
        Map<String, String> displayNameToBase36Id = new HashMap<>();
        items.forEach(item -> {
            String displayName = (String) item.get(Const.DISPLAYNAME);
            String base36Id = (String) item.get(Const.BASE36ID);
            displayNameToBase36Id.put(displayName, base36Id);
        });
        return displayNameToBase36Id;
    }

    /**
     * Creates a placeholder entry for a class code that does not exist in the hierarchy data.
     *
     * @param classCode the class code for which to create the placeholder entry
     * @return a map representing the placeholder entry
     */

    private Map<String, Object> placeholderEntry(String classCode) {
        Map<String, Object> classCodeEntry = new HashMap<>();
        classCodeEntry.put(Const.CLASSCODE, classCode);
        classCodeEntry.put(Const.DISPLAYNAME, null);

        Map<String, Object> hierarchyItem = new HashMap<>();
        hierarchyItem.put("path", null);
        hierarchyItem.put(Const.PARENTBASE36ID, "null");
        hierarchyItem.put(Const.BASE36ID, "null");

        List<Map<String, Object>> hierarchyValues = new ArrayList<>();
        hierarchyValues.add(hierarchyItem);

        classCodeEntry.put(Const.HIERARCHYVALUES, hierarchyValues);

        return classCodeEntry;
    }

    /**
     * Processes the list of items to build a hierarchy structure for the specified class code.
     *
     * @param items                  the list of items to process
     * @param classCode              the class code for which to build the hierarchy
     * @param displayNameToBase36Id  a map of display names to base36 IDs
     * @param classCodeToHierarchy   a map to store the hierarchy structure for each class code
     */
    private void processItemsForHierarchy(List<Map<String, Object>> items, String classCode, Map<String, String> displayNameToBase36Id, Map<String, Map<String, Object>> classCodeToHierarchy) {
        items.stream()
                .filter(item -> classCode.equals(item.get(Const.CLASSCODE)))
                .forEach(item -> {
                    String displayName = (String) item.get(Const.DISPLAYNAME);
                    String base36Id = (String) item.get(Const.BASE36ID);
                    String path = (String) item.get("path");

                    Map<String, Object> hierarchyItem = new HashMap<>();
                    hierarchyItem.put("path", path);
                    hierarchyItem.put(Const.BASE36ID, base36Id);
                    hierarchyItem.put(Const.PARENTBASE36ID, computeParentBase36Id(path, displayNameToBase36Id));

                    if (!classCodeToHierarchy.containsKey(classCode)) {
                        Map<String, Object> classCodeEntry = new HashMap<>();
                        classCodeEntry.put(Const.CLASSCODE, classCode);
                        classCodeEntry.put(Const.DISPLAYNAME, displayName);
                        classCodeEntry.put(Const.HIERARCHYVALUES, new ArrayList<>());
                        classCodeToHierarchy.put(classCode, classCodeEntry);
                    }
                    ((List<Map<String, Object>>) classCodeToHierarchy.get(classCode).get(Const.HIERARCHYVALUES)).add(hierarchyItem);
                });
    }

    /**
     * Computes the parent base36 ID for the given path.
     *
     * @param path                  the path for which to compute the parent base36 ID
     * @param displayNameToBase36Id a map of display names to base36 IDs
     * @return the parent base36 ID
     */

    private String computeParentBase36Id(String path, Map<String, String> displayNameToBase36Id) {
        if (path == null || path.equals("null")) {
            return "null";
        }

        String[] pathParts = path.split("/");
        StringBuilder currentPath = new StringBuilder();

        for (int i = pathParts.length - 1; i >= 0; i--) {
            String pathSegment = pathParts[i].trim();
            if (!pathSegment.isEmpty()) {
                currentPath.insert(0, pathSegment);
                String fullPath = currentPath.toString();
                if (displayNameToBase36Id.containsKey(fullPath)) {
                    return displayNameToBase36Id.get(fullPath);
                }
                currentPath.insert(0, "/");
            }
        }

        return "null";
    }


    /**
     * Fetches all hierarchy data.
     *
     * @return a list of maps representing all hierarchy data
     */

    public List<Map<String, Object>> fetchAllHierarchyData() {

        String query ="SELECT c.displayName, c.base36Id, c.path FROM c WHERE c.pk = 'hierarchy' ORDER BY c.path ASC";


        CosmosPagedIterable<JsonNode> queryResults = container.queryItems(query, new CosmosQueryRequestOptions(), JsonNode.class);

        List<Map<String, Object>> items = new ArrayList<>();

        for (JsonNode item : queryResults) {
            Map<String, Object> itemMap = objectMapper.convertValue(item, new TypeReference<Map<String, Object>>() {});
            items.add(itemMap);
        }

        return getParentBase36Id(items);
    }

    /**
     * Retrieves the parent base36 IDs for items in the hierarchy data.
     *
     * This method processes each item in the provided list to determine its parent in the hierarchy,
     * based on the path and displayName-to-base36Id mappings.
     *
     * @param items the list of items containing hierarchy data with display names, base36 IDs, and paths
     * @return a list of maps, each containing display name, base36 ID, and parent base36 ID
     */
    private List<Map<String, Object>> getParentBase36Id(List<Map<String, Object>> items) {
        List<Map<String, Object>> response = new ArrayList<>();
        Map<String, String> displayNameToBase36Id = new HashMap<>();

        for (Map<String, Object> item : items) {
            String displayName = (String) item.get(Const.DISPLAYNAME);
            String base36Id = (String) item.get(Const.BASE36ID);
            displayNameToBase36Id.put(displayName, base36Id);
        }

        for (Map<String, Object> item : items) {
            String displayName = (String) item.get(Const.DISPLAYNAME);
            String base36Id = (String) item.get(Const.BASE36ID);
            String path = (String) item.get("path");


            Map<String, Object> responseItem = new HashMap<>();
            responseItem.put(Const.DISPLAYNAME, displayName);
            responseItem.put(Const.BASE36ID, base36Id);

            String parentBase36Id = fetchParentBase36Id(path, displayNameToBase36Id);

            responseItem.put(Const.PARENTBASE36ID, parentBase36Id);
            response.add(responseItem);
        }

        return response;
    }

    /**
     * Computes the parent base36 ID based on the given path and displayName-to-base36Id mappings.
     *
     * This method recursively checks the path segments to find the closest parent with a known base36 ID.
     *
     * @param path                 the path of the current item in the hierarchy
     * @param displayNameToBase36Id a map mapping display names to their corresponding base36 IDs
     * @return the parent base36 ID of the current item in the hierarchy
     */

    private String fetchParentBase36Id(String path, Map<String, String> displayNameToBase36Id) {
        if (path == null || path.equals("null")) {
            return "";
        }

        String[] pathParts = path.split("/");
        StringBuilder currentPath = new StringBuilder();


        for (int i = pathParts.length - 1; i >= 0; i--) {
            String pathSegment = pathParts[i].trim();
            if (!pathSegment.isEmpty()) {
                currentPath.insert(0, pathSegment);
                String fullPath = currentPath.toString();
                if (displayNameToBase36Id.containsKey(fullPath)) {
                    return displayNameToBase36Id.get(fullPath);
                }
                currentPath.insert(0, "/");
            }
        }

        return "null";
    }

    /**
     * Retrieves hierarchy data from Cosmos DB based on specified class codes.
     *
     * This method constructs a query to fetch class codes and their corresponding base36 IDs from Cosmos DB.
     * If class codes are provided, it filters the query to include only those class codes.
     * It then converts the retrieved JSON data into a list of maps containing class code and base36 ID pairs.
     * Optionally, it can avoid adding duplicate entries based on class code when {@code avoidDuplicates} is true.
     *
     * @param classCodes      a list of class codes to filter the query results, can be null or empty
     * @param avoidDuplicates a flag indicating whether to avoid duplicate entries based on class code
     * @return a list of maps, each containing class code and base36 ID retrieved from Cosmos DB
     */
    public List<Map<String, Object>> listAllHierarchyData(List<String> classCodes, boolean avoidDuplicates) {
        StringBuilder queryBuilder = new StringBuilder("SELECT c.classCode, c.base36Id FROM c WHERE c.pk = 'hierarchy'");

        if (classCodes != null && !classCodes.isEmpty()) {
            String classCodeFilter = classCodes.stream()
                    .map(code -> "'" + code + "'")
                    .collect(Collectors.joining(", "));
            queryBuilder.append(" AND c.classCode IN (").append(classCodeFilter).append(")");
        }

        CosmosPagedIterable<JsonNode> itemsContainer = container.queryItems(queryBuilder.toString(), new CosmosQueryRequestOptions(), JsonNode.class);

        List<Map<String, Object>> items = new ArrayList<>();

        for (JsonNode item : itemsContainer) {
            Map<String, Object> itemMap = objectMapper.convertValue(item, new TypeReference<Map<String, Object>>() {});
            items.add(itemMap);
        }

        Map<String, String> base36IdMap = new HashMap<>();
        List<Map<String, Object>> results = new ArrayList<>();

        items.forEach(item -> {
            String itemClassCode = (String) item.get(Const.CLASSCODE);
            String base36Id = (String) item.get(Const.BASE36ID);

            if (avoidDuplicates && base36IdMap.containsKey(itemClassCode)) {
                return;
            }

            Map<String, Object> result = new HashMap<>();
            result.put(Const.CLASSCODE, itemClassCode);
            result.put(Const.BASE36ID, base36Id);
            results.add(result);

            base36IdMap.put(itemClassCode, base36Id);
        });

        for (String classCode : classCodes) {
            if (!base36IdMap.containsKey(classCode)) {
                Map<String, Object> result = new HashMap<>();
                result.put(Const.CLASSCODE, classCode);
                result.put(Const.BASE36ID, null);
                results.add(result);
            }
        }

        return results;
    }

}