package Sistema.DAO;

import Sistema.Administrador;
import Sistema.Database.DatabaseConnection;
import Sistema.Participante;
import Sistema.Pessoa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {
    public void salvar(Pessoa usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (nome, senha, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, usuario.getNome());
            pstmt.setString(2, usuario.getSenha() != null ? usuario.getSenha() : "123");
            pstmt.setString(3, (usuario instanceof Administrador) ? "ADMIN" : "PARTICIPANTE");
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    usuario.setId(rs.getInt(1));
                }
            }
        }
    }

    public Pessoa login(String nome, String senha) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE nome = ? AND senha = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, senha);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    Pessoa p;
                    if ("ADMIN".equals(role)) {
                        p = new Administrador(rs.getString("nome"));
                    } else {
                        p = new Participante(rs.getString("nome"));
                    }
                    p.setId(rs.getInt("id"));
                    p.setSenha(rs.getString("senha"));
                    return p;
                }
            }
        }
        return null;
    }

    public List<Participante> listarParticipantes() throws SQLException {
        List<Participante> participantes = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE role = 'PARTICIPANTE' ORDER BY nome";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Participante p = new Participante(rs.getString("nome"));
                p.setId(rs.getInt("id"));
                p.setSenha(rs.getString("senha"));
                participantes.add(p);
            }
        }
        return participantes;
    }

    public Pessoa buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String role = rs.getString("role");
                    Pessoa p;
                    if ("ADMIN".equals(role)) {
                        p = new Administrador(rs.getString("nome"));
                    } else {
                        p = new Participante(rs.getString("nome"));
                    }
                    p.setId(rs.getInt("id"));
                    return p;
                }
            }
        }
        return null;
    }
}
