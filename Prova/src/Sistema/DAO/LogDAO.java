package Sistema.DAO;

import Sistema.Database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogDAO {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void salvar(String mensagem) {
        String sql = "INSERT INTO logs (data_hora, mensagem) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, LocalDateTime.now().format(FMT));
            pstmt.setString(2, mensagem);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro ao salvar log: " + e.getMessage());
        }
    }
}
