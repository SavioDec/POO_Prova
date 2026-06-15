package Sistema.DAO;

import Sistema.Aposta;
import Sistema.Database.DatabaseConnection;
import Sistema.Partida;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ApostaDAO {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private PartidaDAO partidaDAO = new PartidaDAO();

    public void salvar(Aposta aposta, int usuarioId) throws SQLException {
        String sql = "INSERT INTO apostas (usuario_id, partida_id, gols_m, gols_v, data_hora) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, usuarioId);
            pstmt.setInt(2, aposta.getPartida().getId());
            pstmt.setInt(3, aposta.getPrevMandante());
            pstmt.setInt(4, aposta.getPrevVisitante());
            pstmt.setString(5, aposta.getDataHora().format(FMT));
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    aposta.setId(rs.getInt(1));
                }
            }
        }
    }

    public List<Aposta> listarPorUsuario(int usuarioId) throws SQLException {
        List<Aposta> apostas = new ArrayList<>();
        
        class ARow {
            int id, pId, gm, gv;
            String dt;
            ARow(int id, int pId, int gm, int gv, String dt) {
                this.id=id; this.pId=pId; this.gm=gm; this.gv=gv; this.dt=dt;
            }
        }
        List<ARow> rows = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM apostas WHERE usuario_id = ?")) {
            pstmt.setInt(1, usuarioId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    rows.add(new ARow(
                        rs.getInt("id"), rs.getInt("partida_id"),
                        rs.getInt("gols_m"), rs.getInt("gols_v"), rs.getString("data_hora")
                    ));
                }
            }
        }

        // Importante: Cada busca por partida usa sua própria conexão limpa agora.
        for (ARow r : rows) {
            Partida p = partidaDAO.buscarPorId(r.pId);
            if (p != null) {
                LocalDateTime dataHora = LocalDateTime.parse(r.dt, FMT);
                Aposta a = new Aposta(p, r.gm, r.gv, dataHora);
                a.setId(r.id);
                apostas.add(a);
            }
        }
        return apostas;
    }
}
