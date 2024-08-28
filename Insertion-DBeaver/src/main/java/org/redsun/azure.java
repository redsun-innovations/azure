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

    private static void fetchAndStoreData(Connection conn) throws SQLException {
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

        try (PreparedStatement pstmt = conn.prepareStatement(sqlScript);
             ResultSet rs = pstmt.executeQuery()) {

            // Print the result set to console
            while (rs.next()) {
                int displayOrder = rs.getInt("dimval.display_order");
                String dimensionName = rs.getString("dimval.dimension_name");
                String spec = rs.getString("dimval.spec");
                String endecaId = rs.getString("Endeca.Id");
                String displayName = rs.getString("dimval.display_name");
                String endecaAction = rs.getString("Endeca.Action");
                String parentSpec = rs.getString("dimval.parent_spec");
                Timestamp createdTime = rs.getTimestamp("dimval.created_time");
                Timestamp lastUpdatedTime = rs.getTimestamp("dimval.last_updated_time");

                // Print the output to the console
                System.out.printf("Display Order: %d, Dimension Name: %s, Spec: %s, Endeca Id: %s, Display Name: %s, " +
                                "Endeca Action: %s, Parent Spec: %s, Created Time: %s, Last Updated Time: %s%n",
                        displayOrder, dimensionName, spec, endecaId, displayName, endecaAction, parentSpec,
                        createdTime != null ? createdTime.toString() : null,
                        lastUpdatedTime != null ? lastUpdatedTime.toString() : null);
            }
        }
    }

}
