package com.redsun.api.hierarchy.repository;

import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.util.CosmosPagedIterable;
import com.fasterxml.jackson.databind.JsonNode;
import com.redsun.api.hierarchy.entity.HierarchyEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.redsun.api.hierarchy.constant.ConstantTest;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@AutoConfigureMockMvc
class CosmosDbHierarchyRepositoryTest {

    @Mock
    private CosmosContainer container;

    @Mock
    private CosmosPagedIterable<JsonNode> cosmosPagedIterable;

    @Mock
    private HierarchyRepository mockHierarchyRepository;


    private CosmosDbHierarchyRepository hierarchyRepository;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        hierarchyRepository  = new CosmosDbHierarchyRepository(container,mockHierarchyRepository);
    }

    @Test
    void testFetchClassCodeData()  {
        String classCode = "1157";
        List<HierarchyEntity> mockEntities = Arrays.asList(
                new HierarchyEntity("Home Decor", "de018k", "null", "0010"),
                new HierarchyEntity("Fresh Flowers & Houseplants", "mgr0", "Home Decor", "1157")
        );

        when(mockHierarchyRepository.fetchClassCodeData(classCode)).thenReturn(mockEntities);

        // When
        List<Map<String, Object>> result = hierarchyRepository.fetchClassCodeData(classCode);


        assertEquals(1, result.size());
        Map<String, Object> fetchedItem = result.get(0);
        assertEquals("1157", fetchedItem.get(ConstantTest.CLASSCODE));
        assertEquals(ConstantTest.FRESH_FLOWERS, fetchedItem.get(ConstantTest.DISPLAYNAME));

        List<Map<String, Object>> hierarchyValues = (List<Map<String, Object>>) fetchedItem.get("hierarchyValues");

        Map<String, Object> firstHierarchyValue = hierarchyValues.get(0);
        String base36IdInHierarchy = (String) firstHierarchyValue.get(ConstantTest.BASE36ID);
        assertEquals("mgr0", base36IdInHierarchy);
        assertEquals(ConstantTest.HOME_DECOR, firstHierarchyValue.get("path"));
        assertEquals(ConstantTest.DE018K, firstHierarchyValue.get(ConstantTest.PARENTBASE36ID));

    }


    @Test
    void testFetchAllHierarchyData() {

        List<HierarchyEntity> mockEntities = Arrays.asList(
                new HierarchyEntity("Home Decor", "de018k", "null", "0010"),
                new HierarchyEntity("Fresh Flowers", "mgr0", "Home Decor", "1157"),
                new HierarchyEntity("Cut Roses", "mgr1", "Home Decor/Fresh Flowers", "1163")
        );

        when(mockHierarchyRepository.fetchAllHierarchyData()).thenReturn(mockEntities);

        List<Map<String, Object>> result = hierarchyRepository.fetchAllHierarchyData();

        assertEquals(3, result.size());
        Map<String, Object> fetchedItem1 = result.get(0);
        assertEquals(ConstantTest.HOME_DECOR, fetchedItem1.get(ConstantTest.DISPLAYNAME));
        assertEquals(ConstantTest.DE018K, fetchedItem1.get(ConstantTest.BASE36ID));
        Object parentBase36Id = fetchedItem1.get(ConstantTest.PARENTBASE36ID);
        assertTrue(parentBase36Id == null || "".equals(parentBase36Id),
                "Expected parentBase36Id to be null or empty");

    }
    @Test
    void testListAllHierarchyData() {

        List<HierarchyEntity> mockEntities = Arrays.asList(
                new HierarchyEntity(null, "de018k", null, "0010"),
                new HierarchyEntity(null, "mgr0", null, "1157")
        );

        when(mockHierarchyRepository.listAllHierarchyData(anyList(), anyBoolean())).thenReturn(mockEntities);

        List<Map<String, Object>> result = hierarchyRepository.listAllHierarchyData(List.of("0010", "1157"), true);

        assertEquals(2, result.size());
        assertEquals("0010", result.get(0).get(ConstantTest.CLASSCODE));
        assertEquals(ConstantTest.DE018K, result.get(0).get(ConstantTest.BASE36ID));
        assertEquals("1157", result.get(1).get(ConstantTest.CLASSCODE));
        assertEquals("mgr0", result.get(1).get(ConstantTest.BASE36ID));

    }

    @Test
    void testFetchClassCodeDataNotFound() {

        String classCode = "1111";
        List<HierarchyEntity> mockEntities = Collections.emptyList();

        when(mockHierarchyRepository.fetchClassCodeData(classCode)).thenReturn(mockEntities);


        List<Map<String, Object>> result = hierarchyRepository.fetchClassCodeData(classCode);

        assertEquals(1, result.size());
        Map<String, Object> classCodeEntry = result.get(0);
        assertEquals(classCode, classCodeEntry.get(ConstantTest.CLASSCODE));
        assertNull(classCodeEntry.get(ConstantTest.DISPLAYNAME));

        List<Map<String, Object>> hierarchyValues = (List<Map<String, Object>>) classCodeEntry.get("hierarchyValues");
        assertEquals(1, hierarchyValues.size());

        Map<String, Object> hierarchyItem = hierarchyValues.get(0);
        assertNull(hierarchyItem.get("path"));
        assertEquals("null", hierarchyItem.get(ConstantTest.PARENTBASE36ID));
        assertEquals("null", hierarchyItem.get(ConstantTest.BASE36ID));

    }

    @Test
    void testTransformHierarchyData() {
        List<String> base36Ids = Arrays.asList("mgr0", "mgrw");
        List<HierarchyEntity> mockEntities = Arrays.asList(
                new HierarchyEntity("Fresh Flowers & Houseplants",  "mgr0", "For the Home","1157"),
                new HierarchyEntity("Cut Roses",   "mgrw", "For the Home/Fresh Flowers & Houseplants","1163"),
                new HierarchyEntity("Fresh Balsam",  "4z7lv", "For the Home/Fresh Flowers & Houseplants","C136")
        );

        when(mockHierarchyRepository.findHierarchyByBase36Ids(base36Ids)).thenReturn(mockEntities);

        List<Map<String, Object>> expectedTransformedData = Arrays.asList(
                createTransformedData("mgr0", "Fresh Flowers & Houseplants", "1157"),
                createTransformedData("mgrw", "Cut Roses", "1163"),
                createTransformedData("4z7lv", "Fresh Balsam", "C136")
        );


        List<Map<String, Object>> result = hierarchyRepository.findHierarchyByBase36Ids(base36Ids);

        assertEquals(expectedTransformedData, result);
    }

    private Map<String, Object> createTransformedData(String base36Id, String name, String classCode) {
        Map<String, Object> transformedData = new HashMap<>();
        transformedData.put("base36Id", base36Id);
        transformedData.put("name", name);

        Map<String, Object> typeData = new HashMap<>();
        typeData.put("Category", "hierarchy");
        typeData.put("classCode", classCode);
        transformedData.put("type", typeData);

        return transformedData;
    }
}