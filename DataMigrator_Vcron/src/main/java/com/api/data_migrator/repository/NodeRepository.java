package com.api.data_migrator.repository;

import com.api.data_migrator.models.Node;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;


@Repository
public class NodeRepository {

    public Map<String, Node> fetchNodes(Connection conn) throws SQLException {
        String sqlScript = "SELECT * FROM dimval WHERE dimval.last_updated_time > '2023-01-01 00:00:00'";
        Map<String, Node> tree = new HashMap<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sqlScript);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                String spec = rs.getString("dimval.spec");
                String parentSpec = rs.getString("dimval.parent_spec");
                String displayName = rs.getString("dimval.display_name");
                String dimensionName = rs.getString("dimval.dimension_name");
                Timestamp lastUpdatedTime = rs.getTimestamp("dimval.last_updated_time");

                tree.put(spec, new Node(parentSpec, displayName, dimensionName, lastUpdatedTime));
            }
        }
        return tree;
    }
}
