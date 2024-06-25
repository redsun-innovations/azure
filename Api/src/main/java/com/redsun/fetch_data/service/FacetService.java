package com.redsun.fetch_data.service;

import com.redsun.fetch_data.model.FacetGroup;
import com.redsun.fetch_data.repository.FacetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacetService {
    private final FacetRepository facetRepository;

    @Autowired
    public FacetService(FacetRepository facetRepository) {
        this.facetRepository = facetRepository;
    }

    public List<String> getQueryData(String facetType, String facetValue) {
        return facetRepository.getQueryData(facetType, facetValue);
    }
    public List<String> listQueryData(List<FacetGroup> facetGroups) {
        return facetRepository.listQueryData(facetGroups);
    }

    public List<String> searchQueryData(String facetType) {
        return facetRepository.searchQueryData(facetType);
    }
}