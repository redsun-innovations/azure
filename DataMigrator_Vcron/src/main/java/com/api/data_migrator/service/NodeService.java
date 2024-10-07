package com.api.data_migrator.service;


import com.api.data_migrator.models.Node;
import com.api.data_migrator.models.OutputRow;
import com.api.data_migrator.models.PathAndDepth;
import com.api.data_migrator.repository.NodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Service
public class NodeService {

    @Autowired
    private NodeRepository nodeRepository;

    public void processAndSendData(Connection conn, String serviceBusConnectionString, String topicName) throws SQLException {
        Map<String, Node> tree = nodeRepository.fetchNodes(conn);

        // Filter timestamp and process data
        Timestamp filterTime = Timestamp.valueOf("2023-01-01 00:00:00");
        List<OutputRow> outputData = new ArrayList<>();

        for (Map.Entry<String, Node> entry : tree.entrySet()) {
            String spec = entry.getKey();
            Node info = entry.getValue();
            if (info.lastUpdatedTime != null && info.lastUpdatedTime.after(filterTime)) {
                PathAndDepth result = findPathAndDepth(tree, spec);
                OutputRow row = new OutputRow(
                        UUID.randomUUID().toString(),
                        spec,
                        info.displayName,
                        info.dimensionName,
                        info.parentSpec,
                        result.path,
                        result.depth,
                        info.lastUpdatedTime
                );
                outputData.add(row);
            }
        }

        // Send data to Azure Service Bus
        long baseValue = loadLastBase10();
        for (OutputRow row : outputData) {
            long base10 = baseValue + outputData.indexOf(row) + 1;
            String base36Id = Long.toString(base10, 36);
            String jsonMessage = convertToJson(row, base10, base36Id);
            AzureServiceBus.sendToAzureTopic(jsonMessage, serviceBusConnectionString, topicName);
            saveLastBase10(base10);
        }
    }

    private String convertToJson(OutputRow row, long base10, String base36Id) {
        return "{"
                + "\"pk\":\"hierarchy\","
                + "\"name\":\"Category\","
                + "\"displayName\":\"" + row.displayName + "\","
                + "\"classCode\":\"" + row.spec + "\","
                + "\"base10\":\"" + base10 + "\","
                + "\"base36Id\":\"" + base36Id + "\","
                + "\"path\":\"" + row.path + "\","
                + "\"depth\":\"" + row.depth + "\","
                + "\"priority\":\"0\","
                + "\"seoTitle\":\"\","
                + "\"metaDescription\":\"\","
                + "\"metaKeywords\":\"\","
                + "\"seoCanonicalUrl\":\"\","
                + "\"seoContent\":\"\""
                + "}";
    }

    public PathAndDepth findPathAndDepth(Map<String, Node> tree, String spec) {
        List<String> path = new ArrayList<>();
        String currentSpec = spec;
        while (!"/".equals(currentSpec)) {
            Node currentNode = tree.get(currentSpec);
            if (currentNode == null) break;
            if (!currentSpec.equals(spec)) path.add(currentNode.displayName);
            currentSpec = currentNode.parentSpec;
        }
        Collections.reverse(path);
        String pathString = String.join("/", path);
        int depth = path.size();
        return new PathAndDepth(pathString, depth);
    }

    private static final String BASE10_FILE = "lastBase10.txt";

    public long loadLastBase10() {
        try {
            String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(BASE10_FILE)));
            return Long.parseLong(content.trim());
        } catch (Exception e) {
            return 147523717;
        }
    }

    public void saveLastBase10(long base10) {
        try {
            java.nio.file.Files.write(java.nio.file.Paths.get(BASE10_FILE), String.valueOf(base10).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
