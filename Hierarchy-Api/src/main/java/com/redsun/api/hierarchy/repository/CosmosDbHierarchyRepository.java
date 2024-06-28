package com.redsun.api.hierarchy.repository;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.redsun.api.hierarchy.constant.Const;
import org.springframework.stereotype.Repository;

import java.util.*;
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

        CosmosPagedIterable<JsonNode> queryResults = container.queryItems(query, new CosmosQueryRequestOptions(), JsonNode.class);

        List<Map<String, Object>> items = new ArrayList<>();

        for (JsonNode item : queryResults) {
            Map<String, Object> itemMap = objectMapper.convertValue(item, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
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

    private Map<String, String> mapDisplayNameToBase36Id(List<Map<String, Object>> items) {
        Map<String, String> displayNameToBase36Id = new HashMap<>();
        items.forEach(item -> {
            String displayName = (String) item.get(Const.DISPLAYNAME);
            String base36Id = (String) item.get(Const.BASE36ID);
            displayNameToBase36Id.put(displayName, base36Id);
        });
        return displayNameToBase36Id;
    }

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



    public List<Map<String, Object>> fetchAllHierarchyData() {

        String query ="SELECT c.displayName, c.base36Id, c.path FROM c WHERE c.pk = 'hierarchy' ORDER BY c.path ASC";


        CosmosPagedIterable<JsonNode> queryResults = container.queryItems(query, new CosmosQueryRequestOptions(), JsonNode.class);

        List<Map<String, Object>> items = new ArrayList<>();

        for (JsonNode item : queryResults) {
            Map<String, Object> itemMap = objectMapper.convertValue(item, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
            items.add(itemMap);
        }

        return getParentBase36Id(items);
    }

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
            Map<String, Object> itemMap = objectMapper.convertValue(item, new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {});
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