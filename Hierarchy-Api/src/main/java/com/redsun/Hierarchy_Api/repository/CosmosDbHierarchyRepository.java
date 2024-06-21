package com.redsun.Hierarchy_Api.repository;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class CosmosDbHierarchyRepository implements HierarchyRepository {
    private final CosmosContainer container;
    private final ObjectMapper objectMapper;

    public CosmosDbHierarchyRepository(CosmosContainer container) {
        this.container = container;
        this.objectMapper = new ObjectMapper();
    }
    public List<Map<String, Object>> fetchClassCodeData(String classCode) {
        String query = "SELECT c.displayName, c.base36Id, c.path, c.classCode FROM c WHERE c.pk = 'hierarchy' ORDER BY c.path ASC";

        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
        CosmosPagedIterable<Map> queryResults = container.queryItems(query, options, Map.class);

        List<Map<String, Object>> items = new ArrayList<>();
        queryResults.iterableByPage().forEach(page -> {
            for (Map<String, Object> item : page.getResults()) {
                items.add(item);
            }
        });

        List<Map<String, Object>> response = new ArrayList<>();
        Map<String, Map<String, Object>> classCodeToHierarchy = new HashMap<>();
        Map<String, String> displayNameToBase36Id = new HashMap<>();

        items.forEach(item -> {
            String displayName = (String) item.get("displayName");
            String base36Id = (String) item.get("base36Id");
            displayNameToBase36Id.put(displayName, base36Id);
        });

        boolean classCodeFound = false;
        for (Map<String, Object> item : items) {
            String itemClassCode = (String) item.get("classCode");
            if (itemClassCode != null && itemClassCode.equals(classCode)) {
                classCodeFound = true;
                break;
            }
        }

        if (!classCodeFound) {
            Map<String, Object> classCodeEntry = new HashMap<>();
            classCodeEntry.put("classCode", classCode);
            classCodeEntry.put("displayName", null);

            Map<String, Object> hierarchyItem = new HashMap<>();
            hierarchyItem.put("path", null);
            hierarchyItem.put("parentBase36Id", "null");
            hierarchyItem.put("base36Id", "null");

            List<Map<String, Object>> hierarchyValues = new ArrayList<>();
            hierarchyValues.add(hierarchyItem);

            classCodeEntry.put("hierarchyValues", hierarchyValues);
            response.add(classCodeEntry);
            return response;
        }

        items.forEach(item -> {
            String itemClassCode = (String) item.get("classCode");
            if (itemClassCode != null && itemClassCode.equals(classCode)) {
                String displayName = (String) item.get("displayName");
                String base36Id = (String) item.get("base36Id");
                String path = (String) item.get("path");

                Map<String, Object> hierarchyItem = new HashMap<>();
                hierarchyItem.put("path", path);
                hierarchyItem.put("base36Id", base36Id);

                String parentBase36Id = "null";

                if (path != null && !path.equals("null")) {
                    String[] pathParts = path.split("/");
                    StringBuilder currentPath = new StringBuilder();
                    boolean found = false;

                    for (int i = pathParts.length - 1; i >= 0; i--) {
                        String pathSegment = pathParts[i].trim();
                        if (!pathSegment.isEmpty()) {
                            currentPath.insert(0, pathSegment);
                            String fullPath = currentPath.toString();
                            if (displayNameToBase36Id.containsKey(fullPath)) {
                                parentBase36Id = displayNameToBase36Id.get(fullPath);
                                found = true;
                                break;
                            }
                            currentPath.insert(0, "/");
                        }
                    }
                }

                hierarchyItem.put("parentBase36Id", parentBase36Id);

                if (!classCodeToHierarchy.containsKey(classCode)) {
                    Map<String, Object> classCodeEntry = new HashMap<>();
                    classCodeEntry.put("classCode", classCode);
                    classCodeEntry.put("displayName", displayName);
                    classCodeEntry.put("hierarchyValues", new ArrayList<>());
                    classCodeToHierarchy.put(classCode, classCodeEntry);
                }
                ((List<Map<String, Object>>) classCodeToHierarchy.get(classCode).get("hierarchyValues")).add(hierarchyItem);
            }
        });
        response.addAll(classCodeToHierarchy.values());
        return response;
    }


    public List<Map<String, Object>> fetchAllHierarchyData() {

        String query ="SELECT c.displayName, c.base36Id, c.path FROM c WHERE c.pk = 'hierarchy' ORDER BY c.path ASC";

        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
        CosmosPagedIterable<Map> queryResults = container.queryItems(query, options, Map.class);

        List<Map<String, Object>> items = new ArrayList<>();
        queryResults.iterableByPage().forEach(page -> {
            for (Map<String, Object> item : page.getResults()) {
                items.add(item);
            }
        });

        return getParentBase36Id(items);
    }

    private List<Map<String, Object>> getParentBase36Id(List<Map<String, Object>> items) {
        List<Map<String, Object>> response = new ArrayList<>();
        Map<String, String> displayNameToBase36Id = new HashMap<>();

        for (Map<String, Object> item : items) {
            String displayName = (String) item.get("displayName");
            String base36Id = (String) item.get("base36Id");
            displayNameToBase36Id.put(displayName, base36Id);
        }

        for (Map<String, Object> item : items) {
            String displayName = (String) item.get("displayName");
            String base36Id = (String) item.get("base36Id");
            String path = (String) item.get("path");

            Map<String, Object> responseItem = new HashMap<>();
            responseItem.put("displayName", displayName);
            responseItem.put("base36Id", base36Id);

            String parentBase36Id = "";

            if (path != null && !path.equals("null")) {
                String[] pathParts = path.split("/");
                StringBuilder currentPath = new StringBuilder();
                boolean found = false;

                for (int i = pathParts.length - 1; i >= 0; i--) {
                    String pathSegment = pathParts[i].trim();
                    if (!pathSegment.isEmpty()) {
                        currentPath.insert(0, pathSegment);
                        String fullPath = currentPath.toString();
                        if (displayNameToBase36Id.containsKey(fullPath)) {
                            parentBase36Id = displayNameToBase36Id.get(fullPath);
                            found = true;
                            break;
                        }
                        currentPath.insert(0, "/");
                    }
                }

                if (!found) {
                    parentBase36Id = "null";
                }
            }

            responseItem.put("parentBase36Id", parentBase36Id);
            response.add(responseItem);
        }

        return response;
    }

    public List<Map<String, Object>> listAllHierarchyData(List<String> classCodes, boolean avoidDuplicates) {
        StringBuilder queryBuilder = new StringBuilder("SELECT c.classCode, c.base36Id FROM c WHERE c.pk = 'hierarchy'");

        if (classCodes != null && !classCodes.isEmpty()) {
            String classCodeFilter = classCodes.stream()
                    .map(code -> "'" + code + "'")
                    .collect(Collectors.joining(", "));
            queryBuilder.append(" AND c.classCode IN (").append(classCodeFilter).append(")");
        }

        CosmosQueryRequestOptions options = new CosmosQueryRequestOptions();
        CosmosPagedIterable<JsonNode> items = container.queryItems(queryBuilder.toString(), options, JsonNode.class);

        Map<String, String> base36IdMap = new HashMap<>();
        List<Map<String, Object>> results = new ArrayList<>();

        items.forEach(item -> {
            String itemClassCode = item.get("classCode").asText();
            String base36Id = item.get("base36Id").asText();

            if (avoidDuplicates && base36IdMap.containsKey(itemClassCode)) {
                return;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("classCode", itemClassCode);
            result.put("base36Id", base36Id);

            results.add(result);

            base36IdMap.put(itemClassCode, base36Id);
        });

        for (String classCode : classCodes) {
            if (!base36IdMap.containsKey(classCode)) {
                Map<String, Object> result = new HashMap<>();
                result.put("classCode", classCode);
                result.put("base36Id", null);
                results.add(result);
            }
        }

        return results;
    }

}
