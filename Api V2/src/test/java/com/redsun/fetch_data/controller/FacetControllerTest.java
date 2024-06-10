package com.redsun.fetch_data.controller;

import com.redsun.fetch_data.model.FacetGroup;
import com.redsun.fetch_data.service.FacetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FacetController.class)
public class FacetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacetService facetService;

    private List<String> dummyResponse;

    @BeforeEach
    public void setup() {
        dummyResponse = Arrays.asList("base36Id1", "base36Id2");
    }

    @Test
    public void testGetQueryData() throws Exception {
        when(facetService.getQueryData(anyString(), anyString())).thenReturn(dummyResponse);

        mockMvc.perform(get("/v1/api/facet/get")
                        .param("name", "Category")
                        .param("classCode", "1163"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"base36Id1\", \"base36Id2\"]"));

        verify(facetService, times(1)).getQueryData("Category", "1163");
    }

    @Test
    public void testListQueryData() throws Exception {
        when(facetService.listQueryData(anyList())).thenReturn(dummyResponse);

        String requestBody = "[{\"name\":\"Category\",\"classCode\":\"H220\"},{\"name\":\"Category\",\"classCode\":\"1163\"},{\"name\":\"Category\",\"classCode\":\"H157\"}]";

        mockMvc.perform(post("/v1/api/facet/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"base36Id1\", \"base36Id2\"]"));

        verify(facetService, times(1)).listQueryData(anyList());
    }

    @Test
    public void testSearchQueryData() throws Exception {
        when(facetService.searchQueryData(anyString())).thenReturn(dummyResponse);

        mockMvc.perform(get("/v1/api/facet/search")
                        .param("name", "Category"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"base36Id1\", \"base36Id2\"]"));

        verify(facetService, times(1)).searchQueryData("Category");
    }

}
