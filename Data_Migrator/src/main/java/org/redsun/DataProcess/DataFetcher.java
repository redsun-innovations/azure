package org.redsun.DataProcess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.redsun.models.OutputRow;
import org.redsun.models.Node;
import org.redsun.models.PathAndDepth;
import org.redsun.AzureService. AzureServiceBus;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;

public class DataFetcher {
    //private static final Logger Logger LoggerFactory.getLogger(org.example.DataFetcher.class);
    public static void fetchAndStoreData(Connection conn, String serviceBusConnectionString, String topicName) throws SQLException {
        // Define your SQL script
        String sqlScript = "";

        Timestamp filterTime = Timestamp.valueOf( "2023-01-01 00:00:00");
        Map<String, Node> tree = new HashMap<>();
        try (PreparedStatement pstmt = conn.prepareStatement(sqlScript);
             ResultSet rs = pstmt.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                String spec = rs.getString("dimval.spec");
                String parentSpec = rs.getString( "dimval.parent_spec");
                String displayName = rs.getString( "dimval.display_name");
                String dimensionName = rs.getString( "dimval.dimension_name");
                Timestamp lastUpdatedTime = rs.getTimestamp ( "dimval.last_updated_time");

                System.out.println("spec: "+spec);
                System.out.println("parentId: "+parentSpec);
                System.out.println("displayName: "+displayName);
                System.out.println("dimensionName: "+ dimensionName);

                tree.put(spec, new Node(parentSpec, displayName, dimensionName, lastUpdatedTime));
                count++;
            }
            System.out.println("count is: "+ count);
            // Calculate path and depth for each node
            List<OutputRow> outputData = new ArrayList<>();
            for (Map.Entry<String, Node> entry : tree.entrySet()) {
                String spec = entry.getKey();
                Node info = entry.getValue();
                if(info.lastUpdatedTime != null && info.lastUpdatedTime.after(filterTime)) {
                    PathAndDepth result = findPathAndDepth(tree, spec);
                    OutputRow row= new OutputRow(
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
            // Print the results or store them as needed
            int countWithPath = 0;
            long baseValue = loadLastBase10();
            for (OutputRow row : outputData) {
                countWithPath++;
                long base10 = baseValue + countWithPath;
                String base36Id = Long.toString(base10,  36);
                String jsonMessage = convertToJson(row, base10, base36Id);
                System.out.println("Json message is: "+ jsonMessage);
                if(jsonMessage != null) {
                    AzureServiceBus.sendToAzureTopic (jsonMessage, serviceBusConnectionString, topicName);
                }

                //save the Last Base10 Value
                Long lastBase10 = baseValue + countWithPath;
                saveLastBase10(lastBase10);
            }
            System.out.println("CountwithPath" + countWithPath);
        } catch (SQLException e) {
            throw e;
        }
    }
    //Building Json Structure For Azure Topics
    private static String convertToJson (OutputRow row, long base10, String base36Id) {
        return "{"
                 + "\"pk\":\"hierarchy\","
                + "\"name\":\"Category\","
                + "\"displayName\":\"" + row.displayName + "\","
                + "\"classCode\":\"" + row.spec + "\","
                + "\"base10\":\"" + base10 + "\","
                + "\"bas36Id\":\"" + base36Id + "\","
                + "\"path\":\"" + row.path + "\","
                + "\"depth\":\"" + row.depth + "\""
                +"\"priority\":\"0\","
                +"\"seoTitle\":\"\","
                +"\"metaDescription\":\"\","
                +"\"metaKeywords\":\"\","
                +"\"seoCanonicalUrl\":\"\","
                +"\"seoContent\":\"\""
                + "}";
    }
    //Finding Path and Depth
    public static PathAndDepth findPathAndDepth (Map<String, Node> tree, String spec) {
    List<String> path = new ArrayList<>();
    String currentSpec = spec;

    while (!"/".equals(currentSpec)) {
        Node currentNode = tree.get(currentSpec);
        if (currentNode == null) break;
        // Do not add the last element (current display name) to the path
        if (!currentSpec.equals(spec)) { path.add(currentNode.displayName);
        }
        currentSpec = currentNode.parentSpec;
    }
        Collections.reverse(path);
        int depth = path.size(); // Adjust depth since the last element is excluded
        String pathString = String.join( "/", path);
        return new PathAndDepth(pathString, depth);
    }
    //Loading LastBase10 and Processing
    private static final String BASE10_FILE = "lastBase10.txt";
    public static long loadLastBase10() {
        File file = new File(BASE10_FILE);
        if (file.exists()) {
            try {
                String content = new String(Files.readAllBytes(Paths.get(BASE10_FILE)));
                return Long.parseLong(content.trim());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //if the file doesn't exist reurn base Value
        return 147523717;
    }

        //save the current base10 Value to the file
        public static void saveLastBase10 (long base10){
            try {
                Files.write(Paths.get(BASE10_FILE), String.valueOf(base10).getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
                