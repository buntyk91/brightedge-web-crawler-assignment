package core;

import java.sql.*;
import java.util.*;

public class UrlFetcher {
    private final Connection conn;

    public UrlFetcher(Connection conn) {
        this.conn = conn;
    }

    public List<String> fetchNextBatch(int limit) throws SQLException {
        List<String> urls = new ArrayList<>();
        String sql = "SELECT url FROM url_table WHERE status = 'PENDING' LIMIT ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, limit);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            urls.add(rs.getString("url"));
        }
        return urls;
    }
}
