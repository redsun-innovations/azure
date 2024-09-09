package org.redsun;

import org.redsun.service.CosmosDbService;
import org.redsun.model.Hierarchy;
import org.redsun.util.CsvUtil;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Create a service instance
        CosmosDbService cosmosDbService = new CosmosDbService();

        // Fetch data using the default partition key defined in CosmosDbService
        List<Hierarchy> hierarchies = cosmosDbService.getHierarchies();

        // Print results to console
        hierarchies.forEach(System.out::println);

        // Write results to CSV file
        String csvFilePath = "output/hierarchies.csv";
        CsvUtil.writeToCsv(hierarchies, csvFilePath);
    }
}
