package com.redsun.api.hierarchy.controller;

import com.redsun.api.hierarchy.service.FacetService;
import com.redsun.api.hierarchy.service.HierarchyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@RestController
//@RequestMapping("/group")
//public class GroupController {
//
//    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);
//
//    private final HierarchyService hierarchyService;
//    private final FacetService facetService;
//
//    @Autowired
//    public GroupController(HierarchyService hierarchyService, FacetService facetService) {
//        this.hierarchyService = hierarchyService;
//        this.facetService = facetService;
//    }
//
//    @GetMapping("/fetchGroupData")
//    public Map<String, List<Map<String, Object>>> fetchCombinedData(@RequestParam(value = "base36Ids") String base36Ids) {
//        logger.info("fetchCombinedData called with base36Ids: {}", base36Ids);
//        Map<String, List<Map<String, Object>>> combinedResults = new HashMap<>();
//        try {
//            List<Map<String, Object>> hierarchyData = hierarchyService.fetchHierarchyData(base36Ids);
//            List<Map<String, Object>> facetData = facetService.fetchFacetData(base36Ids);
//
//            combinedResults.put("hierarchyData", hierarchyData);
//            combinedResults.put("facetData", facetData);
//
//            logger.info("fetchCombinedData completed successfully with results");
//            return combinedResults;
//        } catch (Exception e) {
//            logger.error("Error occurred while fetching combined data",base36Ids, e.getMessage(),e);
//            return Collections.emptyMap();
//        }
//    }
//}



@RestController
@RequestMapping("/v1/group")
public class GroupController {

    private static final Logger logger = LoggerFactory.getLogger(GroupController.class);

    private final HierarchyService hierarchyService;
    private final FacetService facetService;

    @Autowired
    public GroupController(HierarchyService hierarchyService, FacetService facetService) {
        this.hierarchyService = hierarchyService;
        this.facetService = facetService;
    }


    @GetMapping("/fetchHierarchyData")
    public Map<String, List<Map<String, Object>>> fetchHierarchyData(@RequestParam(value = "base36Ids") String base36Ids) {
        logger.info("fetchHierarchyData called with base36Ids: {}", base36Ids);
        Map<String, List<Map<String, Object>>> combinedResults = new HashMap<>();
        try {
            List<Map<String, Object>> hierarchyData = hierarchyService.fetchHierarchyData(base36Ids);
            List<Map<String, Object>> facetData = facetService.fetchFacetData(base36Ids);
            combinedResults.put("hierarchyData", hierarchyData);
            combinedResults.put("facetData", facetData);
            logger.info("fetchHierarchyData completed successfully with results");
            return combinedResults;
        } catch (Exception e) {
            logger.error("Error occurred while fetching hierarchy data with Exception:{}",base36Ids,e.getMessage(), e);
            return Collections.emptyMap();
        }

    }

    public static String extractBase36IdsFromUrl (String url) {
        String pattern = "/N-(.*?)/";
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher matcher = regex.matcher(url);

        return matcher.find() ? matcher.group(1) : null;
    }

    @GetMapping("/fetchGroupId")
    public String fetchGroupId(@RequestParam(value = "endecaUrl") String endecaUrl) {
        logger.info("fetchGroupId from endecaUrl: {}", endecaUrl);

        String base36Ids = extractBase36IdsFromUrl(endecaUrl);
        if (base36Ids == null) {
            logger.debug("No N-dash found");
            return null;
        }

        try {
            List<Map<String, Object>> hierarchyData = hierarchyService.fetchHierarchyData(base36Ids);

            if (hierarchyData.isEmpty()) {
                logger.debug("fetchHierarchyData returned an empty list");
                return null;
            }

            for (Map<String, Object> map : hierarchyData) {
                String id = map.get("base36Id").toString();
                if (!id.isBlank()) {
                    return id;
                }
            }
        } catch (Exception e) {
            logger.error("Exception occurred while fetching hierarchy data: {}", e.getMessage());
        }

        return null;
    }

    @GetMapping("/createCioUrls")
    public Map<String, String> createCioUrls(@RequestParam(value = "endecaUrl") String endecaUrl) {
        logger.info("createCioUrls called with endecaUrl: {}", endecaUrl);

        Map<String, String> cioUrls = new HashMap<>();
        cioUrls.put("NavigationState", endecaUrl.replaceAll("^/(.?)/_/N-(.?)/(.*)$", "/c/$1/-/$2/c.html"));
        cioUrls.put("NavigationStateJson", "");

        String groupId = fetchGroupId(endecaUrl);
        if (!groupId.isBlank()) {
            cioUrls.put("NavigationStateJson", "?groupId=" + groupId);
        }

        return cioUrls;
    }
}