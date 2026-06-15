package Sistema.DAO;

import Sistema.Database.DatabaseConnection;
import Sistema.GrupoAposta;
import Sistema.Participante;
import Sistema.Pessoa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GrupoDAO {
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public void salvar(GrupoAposta grupo) throws SQLException {
        String sql = "INSERT INTO grupos (nome, criador_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, grupo.getNome());
            pstmt.setInt(2, grupo.getCriador().getId());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    grupo.setId(rs.getInt(1));
                }
            }
        }
        adicionarUsuarioAoGrupo(grupo.getId(), grupo.getCriador().getId());
    }

    public void adicionarUsuarioAoGrupo(int grupoId, int usuarioId) throws SQLException {
        String sql = "INSERT OR IGNORE INTO grupo_usuarios (grupo_id, usuario_id) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, grupoId);
            pstmt.setInt(2, usuarioId);
            pstmt.executeUpdate();
        }
    }

    public List<GrupoAposta> listarTodos() throws SQLException {
        List<GrupoAposta> grupos = new ArrayList<>();
        
        // Estrutura para segurar dados brutos
        class GrupoRow {
            int id, cId; String nome;
            GrupoRow(int id, String nome, int cId) { this.id=id; this.nome=nome; this.cId=cId; }
        }
        List<GrupoRow> rows = new ArrayList<>();

        // Passo 1: Abre conexão, lê tudo de 'grupos', FECHA conexão.
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM grupos")) {
            while (rs.next()) {
                rows.add(new GrupoRow(rs.getInt("id"), rs.getString("nome"), rs.getInt("criador_id")));
            }
        }

        // Passo 2: Processa os dados fora do ResultSet aberto
        for (GrupoRow row : rows) {
            Pessoa p = usuarioDAO.buscarPorId(row.cId);
            if (p instanceof Participante) {
                GrupoAposta g = new GrupoAposta(row.nome, (Participante) p);
                g.setId(row.id);
                carregarParticipantes(g);
                grupos.add(g);
            }
        }
        return grupos;
    }

    private void carregarParticipantes(GrupoAposta grupo) throws SQLException {
        List<Integer> userIds = new ArrayList<>();
        // Passo 1: Busca IDs, FECHA conexão.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT usuario_id FROM grupo_usuarios WHERE grupo_id = ?")) {
            pstmt.setInt(1, grupo.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) userIds.add(rs.getInt("usuario_id"));
            }
        }

        // Passo 2: Busca objetos, cada um em sua própria conexão limpa.
        for (Integer uid : userIds) {
            Pessoa p = usuarioDAO.buscarPorId(uid);
            if (p instanceof Participante && !p.getNome().equals(grupo.getCriador().getNome())) {
                grupo.adicionarParticipante((Participante) p);
            }
        }
    }
}
