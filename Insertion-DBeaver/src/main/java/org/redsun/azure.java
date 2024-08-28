package org.redsun;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;


public class azure {
    private static final Logger logger = LoggerFactory.getLogger(AzureMigration.class);

    private static final String DATABASE_URL = "jdbc:sqlserver://localhost:1433;databaseName=master;integratedSecurity=true;encrypt=false;";
    private static final LocalDateTime SPECIFIC_TIME = LocalDateTime.of(2024, 8, 22, 12, 0); // Example: August 22, 2024, 12:00 PM

    public static void main(String[] args) {
        logger.info("Starting data migration process...");

        try (Connection conn = DriverManager.getConnection(DATABASE_URL)) {
            fetchAndStoreData(conn);
            logger.info("Data migration process completed.");
        } catch (SQLException e) {
            logger.error("SQL Exception occurred: " + e.getMessage(), e);
        }
    }

    private static void fetchAndStoreData(Connection conn) throws SQLException {
        logger.debug("Fetching data from the table, records updated after: {}", SPECIFIC_TIME);

        // Query to fetch rows where last_updated_time is greater than the specific time
        String query = "SELECT dimval_spec, dimval_display_order, dimval_dimension_name, Endeca_Id, dimval_display_name, Endeca_Action, dimval_parent_spec, dimval_created_time, dimval_last_updated_time " +
                "FROM YourTableName WHERE dimval_last_updated_time > ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(SPECIFIC_TIME));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String dimvalSpec = rs.getString("dimval_spec");
                    int displayOrder = rs.getInt("dimval_display_order");
                    String dimensionName = rs.getString("dimval_dimension_name");
                    String endecaId = rs.getString("Endeca_Id");
                    String displayName = rs.getString("dimval_display_name");
                    String action = rs.getString("Endeca_Action");
                    String parentSpec = rs.getString("dimval_parent_spec");
                    Timestamp createdTime = rs.getTimestamp("dimval_created_time");
                    Timestamp lastUpdatedTime = rs.getTimestamp("dimval_last_updated_time");

                    String actionDetermined = determineAction(createdTime, lastUpdatedTime);
                    logger.debug("Found record - Spec: {}, DisplayOrder: {}, DimensionName: {}, EndecaId: {}, DisplayName: {}, Action: {}, ParentSpec: {}, CreatedTime: {}, LastUpdatedTime: {}, DeterminedAction: {}",
                            dimvalSpec, displayOrder, dimensionName, endecaId, displayName, action, parentSpec, createdTime, lastUpdatedTime, actionDetermined);

                    storeData(conn, dimvalSpec, displayOrder, dimensionName, endecaId, displayName, action, parentSpec, createdTime, lastUpdatedTime, actionDetermined);
                }
            }
        }
    }

    private static String determineAction(Timestamp createdAt, Timestamp updatedAt) {
        LocalDateTime createdAtTime = createdAt.toLocalDateTime();
        LocalDateTime updatedAtTime = updatedAt.toLocalDateTime();
        return createdAtTime.equals(updatedAtTime) ? "Inserted" : "Updated";
    }

    private static void storeData(Connection conn, String dimvalSpec, int displayOrder, String dimensionName, String endecaId, String displayName, String action, String parentSpec, Timestamp createdTime, Timestamp lastUpdatedTime, String actionDetermined) throws SQLException {
        String insertQuery = "INSERT INTO DestinationTable (dimval_spec, dimval_display_order, dimval_dimension_name, Endeca_Id, dimval_display_name, Endeca_Action, dimval_parent_spec, dimval_created_time, dimval_last_updated_time, ActionDetermined) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(insertQuery)) {
            pstmt.setString(1, dimvalSpec);
            pstmt.setInt(2, displayOrder);
            pstmt.setString(3, dimensionName);
            pstmt.setString(4, endecaId);
            pstmt.setString(5, displayName);
            pstmt.setString(6, action);
            pstmt.setString(7, parentSpec);
            pstmt.setTimestamp(8, createdTime);
            pstmt.setTimestamp(9, lastUpdatedTime);
            pstmt.setString(10, actionDetermined);
            pstmt.executeUpdate();
            logger.debug("Inserted record - Spec: {}, DisplayOrder: {}, DimensionName: {}, EndecaId: {}, DisplayName: {}, Action: {}, ParentSpec: {}, CreatedTime: {}, LastUpdatedTime: {}, DeterminedAction: {}",
                    dimvalSpec, displayOrder, dimensionName, endecaId, displayName, action, parentSpec, createdTime, lastUpdatedTime, actionDetermined);
        }
    }

    import java.sql.*;
import java.util.*;

    public class HierarchyBuilder {

        private static final Logger logger = LoggerFactory.getLogger(HierarchyBuilder.class);

        public static void fetchAndStoreData(Connection conn) throws SQLException {
            logger.debug("Fetching data from the CLASS_NOTE table, records updated after: {}", SPECIFIC_TIME);

            // Define your SQL script
            String sqlScript = "WITH temp_hier(Parent_Class_Id, Parent_Class_Code, Child_Class_Id, Child_Class_Disp_Name, " +
                    "Child_Class_Code, Child_Class_Type, Class_Seq_NBR, CRTE_TMS, LAST_UPD_TMS) AS " +
                    "(SELECT cs.prnt_class_id, rtrim(caiParent.CLASS_ALT_CD) AS Parent_Class_Code, cs.child_class_id, " +
                    "rtrim(cnChild.class_note_txt) AS Class_Display_Name, cast(rtrim(caiChild.class_alt_cd) as varchar) AS Class_Code, " +
                    "cast(rtrim(cmcChild.app_misc_cd) as varchar), 1, cnChild.CRTE_TMS, cnChild.LAST_UPD_TMS " +
                    "FROM q.class_struct cs with(nolock) " +
                    "INNER JOIN q.class cChild with(nolock) on cChild.class_id = cs.child_class_id " +
                    "INNER JOIN q.class_note cnChild with(nolock) on cnChild.class_id = cs.child_class_id " +
                    "INNER JOIN q.class_alt_id caiChild with(nolock) on caiChild.class_id = cs.child_class_id " +
                    "INNER JOIN q.class_misc_code cmcChild with(nolock) on cmcChild.class_id = cs.child_class_id " +
                    "INNER JOIN q.class cParent with(nolock) ON cParent.class_id = cs.PRNT_CLASS_ID " +
                    "INNER JOIN q.class_note cnParent with(nolock) ON cnParent.class_id = cs.PRNT_CLASS_ID " +
                    "INNER JOIN q.class_alt_id caiParent with(nolock) ON caiParent.class_id = cs.PRNT_CLASS_ID " +
                    "WHERE cs.class_struct_cd = 'CORPC' " +
                    "AND cnChild.class_note_typ_cd = 'CLSDN' " +
                    "AND cs.prim_prnt_ind = 'Y' " +
                    "AND cmcChild.app_misc_cd_typ_cd = 'DRILL' " +
                    "AND cmcChild.app_misc_cd = '1' " +
                    "AND cnParent.class_note_typ_cd = 'CLSDN' " +
                    "AND cs.child_class_id IN (58103,58772,58779,58780,58781,58782,58783,58785,58788, 58911,69605, 58784,69921,2000008) " +
                    "UNION ALL " +
                    "SELECT cs2.prnt_class_id, rtrim(caiParent.class_alt_cd), cs2.child_class_id, " +
                    "rtrim(cnChild.class_note_txt) AS Class_Display_Name, cast(rtrim(caiChild.class_alt_cd) as varchar) as Class_Code, " +
                    "cast(cmcChild.app_misc_cd as varchar), cs2.CLASS_SEQ_NBR AS Class_Seq_NBR, cnChild.CRTE_TMS, cnChild.LAST_UPD_TMS " +
                    "FROM temp_hier th, q.class_struct cs2 with(nolock) " +
                    "INNER JOIN q.class cChild with(nolock) on cChild.class_id = cs2.child_class_id " +
                    "INNER JOIN q.class_note cnChild with(nolock) on cnChild.class_id = cs2.child_class_id " +
                    "INNER JOIN q.class_alt_id caiChild with(nolock) on caiChild.class_id = cs2.child_class_id " +
                    "INNER JOIN q.class_misc_code cmcChild with(nolock) on cmcChild.class_id = cs2.child_class_id " +
                    "INNER JOIN q.class cParent with(nolock) ON cParent.class_id = cs2.PRNT_CLASS_ID " +
                    "INNER JOIN q.class_note cnParent with(nolock) ON cnParent.class_id = cs2.PRNT_CLASS_ID " +
                    "INNER JOIN q.class_alt_id caiParent with(nolock) ON caiParent.class_id = cs2.PRNT_CLASS_ID " +
                    "INNER JOIN q.CLASS_MISC_CODE cmc2 with(nolock) ON CMC2.CLASS_ID = cs2.child_class_id " +
                    "WHERE th.child_class_id = cs2.prnt_class_id " +
                    "AND cs2.class_struct_cd = 'CORPC' " +
                    "AND cnChild.class_note_typ_cd = 'CLSDN' " +
                    "AND cmcChild.app_misc_cd_typ_cd = 'DRILL' " +
                    "AND cnParent.class_note_typ_cd = 'CLSDN' " +
                    "AND cs2.disp_ind = 'Y' " +
                    "AND cmc2.APP_MISC_CD_TYP_CD = 'GROUP' " +
                    "AND cmc2.APP_MISC_CD = '1') " +
                    "SELECT 1 as 'dimval.display_order', 'Category' as 'dimval.dimension_name', " +
                    "rtrim(caiChild.class_alt_cd) as 'dimval.spec', concat('Category:', cast((rtrim(caiChild.class_alt_cd)) as varchar)) as 'Endeca.Id', " +
                    "rtrim(cnchild.CLASS_NOTE_TXT) as 'dimval.display_name', 'UPSERT' as 'Endeca.Action', '/' as 'dimval.parent_spec', " +
                    "cnChild.CRTE_TMS AS 'dimval.created_time', cnChild.LAST_UPD_TMS AS 'dimval.last_updated_time' " +
                    "FROM q.class_struct cs with(nolock) " +
                    "INNER JOIN q.class cChild with(nolock) on cChild.class_id = cs.child_class_id " +
                    "INNER JOIN q.class_note cnChild with(nolock) on cnChild.class_id = cs.child_class_id " +
                    "INNER JOIN q.class_alt_id caiChild with(nolock) on caiChild.class_id = cs.child_class_id " +
                    "INNER JOIN q.class_misc_code cmcChild with(nolock) on cmcChild.class_id = cs.child_class_id " +
                    "INNER JOIN q.CLASS_ALT_ID caiParent with(nolock) ON caiParent.CLASS_ID = cs.prnt_class_id " +
                    "WHERE cs.class_struct_cd = 'CORPC' " +
                    "AND cnChild.class_note_typ_cd = 'CLSDN' " +
                    "AND cs.prim_prnt_ind = 'Y' " +
                    "AND cmcChild.app_misc_cd_typ_cd = 'DRILL' " +
                    "AND cmcChild.app_misc_cd = '1' " +
                    "AND cs.child_class_id IN (58103,58772,58779,58780,58781,58782,58783,58785,58788, 58911,69605, 58784,69921,2000008) " +
                    "UNION ALL " +
                    "SELECT th.Class_Seq_NBR as 'dimval.display_order', 'Category' as 'dimval.dimension_name', " +
                    "rtrim(Child_Class_Code) as 'dimval.spec', concat('Category:', Child_Class_Code) as 'Endeca.Id', " +
                    "rtrim(child_class_disp_name) as 'dimval.display_name', 'UPSERT' as 'Endeca.Action', " +
                    "rtrim(Parent_Class_Code) as 'dimval.parent_spec', th.LAST_UPD_TMS AS 'dimval.last_updated_time', " +
                    "th.CRTE_TMS AS 'dimval_created_time' " +
                    "FROM temp_hier th " +
                    "WHERE th.child_class_type IN ('2', '3') " +
                    "ORDER BY 'dimval.display_order', 'dimval.spec'";

            // Define the timestamp to filter results
            Timestamp filterTimestamp = Timestamp.valueOf("2013-03-21 00:00:00");

            Map<String, Node> nodes = new HashMap<>();

            try (PreparedStatement pstmt = conn.prepareStatement(sqlScript);
                 ResultSet rs = pstmt.executeQuery()) {

                // Fetch rows and store in nodes map
                while (rs.next()) {
                    Timestamp lastUpdatedTime = rs.getTimestamp("dimval.last_updated_time");

                    // Apply the timestamp filter based on the lastUpdatedTime
                    if (lastUpdatedTime != null && lastUpdatedTime.after(filterTimestamp)) {

                        String id = rs.getString("dimval.spec");
                        String parentId = rs.getString("dimval.parent_spec");
                        String name = rs.getString("dimval.display_name");
                        Timestamp createdTime = rs.getTimestamp("dimval.created_time");
                        Timestamp updatedTime = rs.getTimestamp("dimval.last_updated_time");

                        Node node = new Node();
                        node.setId(id);
                        node.setName(name);
                        node.setParentId(parentId);
                        node.setCreatedTime(createdTime);
                        node.setUpdatedTime(updatedTime);

                        nodes.put(id, node);
                    }
                }

                // Build hierarchy
                Map<String, List<Node>> hierarchy = new HashMap<>();
                for (Node node : nodes.values()) {
                    hierarchy.computeIfAbsent(node.getParentId(), k -> new ArrayList<>()).add(node);
                }

                // Compute paths and depths
                for (Node node : nodes.values()) {
                    computePathAndDepth(node, hierarchy);
                }

                // Store or print the nodes with their computed paths and depths
                for (Node node : nodes.values()) {
                    logger.debug("Node ID: {}, Name: {}, Path: {}, Depth: {}", node.getId(), node.getName(), node.getPath(), node.getDepth());
                    // Add your logic to store or process nodes here
                }

            } catch (SQLException e) {
                logger.error("Error fetching data", e);
                throw e;
            }
        }

        private static void computePathAndDepth(Node node, Map<String, List<Node>> hierarchy) {
            // Compute the path and depth
            List<Node> path = new ArrayList<>();
            int depth = 0;

            Node current = node;
            while (current != null) {
                path.add(current);
                depth++;
                current = hierarchy.get(current.getParentId()) != null && !hierarchy.get(current.getParentId()).isEmpty() ? hierarchy.get(current.getParentId()).get(0) : null;
            }

            Collections.reverse(path);
            node.setPath(path.stream().map(Node::getName).collect(Collectors.joining(" > ")));
            node.setDepth(depth - 1);
        }

        public static class Node {
            private String id;
            private String name;
            private String parentId;
            private Timestamp createdTime;
            private Timestamp updatedTime;
            private String path;
            private int depth;

            // Getters and setters
            // Add any additional methods if needed

            public String getId() { return id; }
            public void setId(String id) { this.id = id; }

            public String getName() { return name; }
            public void setName(String name) { this.name = name; }

            public String getParentId() { return parentId; }
            public void setParentId(String parentId) { this.parentId = parentId; }

            public Timestamp getCreatedTime() { return createdTime; }
            public void setCreatedTime(Timestamp createdTime) { this.createdTime = createdTime; }

            public Timestamp getUpdatedTime() { return updatedTime; }
            public void setUpdatedTime(Timestamp updatedTime) { this.updatedTime = updatedTime; }

            public String getPath() { return path; }
            public void setPath(String path) { this.path = path; }

            public int getDepth() { return depth; }
            public void setDepth(int depth) { this.depth = depth; }
        }
    }

}
