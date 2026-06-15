package Sistema.DAO;

import Sistema.Database.DatabaseConnection;
import Sistema.Time;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TimeDAO {
    public void salvar(Time time) throws SQLException {
        String sql = "INSERT INTO times (nome) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, time.getNome());
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    time.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Time> listarTodos() throws SQLException {
        List<Time> times = new ArrayList<>();
        String sql = "SELECT * FROM times ORDER BY nome";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Time t = new Time(rs.getString("nome"));
                t.setId(rs.getInt("id"));
                times.add(t);
            }
        }
        return times;
    }

    public Time buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM times WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Time t = new Time(rs.getString("nome"));
                    t.setId(rs.getInt("id"));
                    return t;
                }
            }
        }
        return null;
    }
}
