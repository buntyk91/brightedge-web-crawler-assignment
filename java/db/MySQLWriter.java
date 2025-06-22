package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class MySQLWriter {
    private final Connection conn;

    public MySQLWriter(Connection conn) {
        this.conn = conn;
    }

    public void write(Map<String, Object> doc) throws SQLException {
        String sql = "INSERT INTO parsed_data (url, title, description, body, timestamp) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, (String) doc.get("url"));
        stmt.setString(2, (String) doc.get("title"));
        stmt.setString(3, (String) doc.get("description"));
        stmt.setString(4, (String) doc.get("body"));
        stmt.setString(5, (String) doc.get("timestamp"));
        stmt.executeUpdate();
    }
}
