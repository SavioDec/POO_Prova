package Sistema.DAO;

import Sistema.Database.DatabaseConnection;
import Sistema.Partida;
import Sistema.Time;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PartidaDAO {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private TimeDAO timeDAO = new TimeDAO();

    public void salvar(Partida partida) throws SQLException {
        String sql = "INSERT INTO partidas (mandante_id, visitante_id, data_hora, gols_m, gols_v, finalizada) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, partida.getMandante().getId());
            pstmt.setInt(2, partida.getVisitante().getId());
            pstmt.setString(3, partida.getDataHoraInicio().format(FMT));
            pstmt.setInt(4, partida.getGolsMandante());
            pstmt.setInt(5, partida.getGolsVisitante());
            pstmt.setInt(6, partida.isFinalizada() ? 1 : 0);
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    partida.setId(rs.getInt(1));
                }
            }
        }
    }

    public void atualizar(Partida partida) throws SQLException {
        String sql = "UPDATE partidas SET gols_m = ?, gols_v = ?, finalizada = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, partida.getGolsMandante());
            pstmt.setInt(2, partida.getGolsVisitante());
            pstmt.setInt(3, partida.isFinalizada() ? 1 : 0);
            pstmt.setInt(4, partida.getId());
            pstmt.executeUpdate();
        }
    }

    public List<Partida> listarTodas() throws SQLException {
        List<Partida> partidas = new ArrayList<>();
        String sql = "SELECT * FROM partidas";
        
        // Estrutura temporária para carregar sem fechar o ResultSet antes da hora
        class Row {
            int id, mId, vId, gm, gv, fin;
            String dt;
            Row(int id, int mId, int vId, String dt, int gm, int gv, int fin) {
                this.id=id; this.mId=mId; this.vId=vId; this.dt=dt; this.gm=gm; this.gv=gv; this.fin=fin;
            }
        }
        List<Row> rows = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(new Row(
                    rs.getInt("id"), rs.getInt("mandante_id"), rs.getInt("visitante_id"),
                    rs.getString("data_hora"), rs.getInt("gols_m"), rs.getInt("gols_v"), rs.getInt("finalizada")
                ));
            }
        }

        for (Row r : rows) {
            Time mandante = timeDAO.buscarPorId(r.mId);
            Time visitante = timeDAO.buscarPorId(r.vId);
            LocalDateTime dataHora = LocalDateTime.parse(r.dt, FMT);
            
            Partida p = new Partida(mandante, visitante, dataHora);
            p.setId(r.id);
            if (r.fin == 1) {
                p.finalizarPartida(r.gm, r.gv);
            }
            partidas.add(p);
        }
        return partidas;
    }

    public Partida buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM partidas WHERE id = ?";
        Integer mId = null, vId = null, gm = null, gv = null, fin = null;
        String dt = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    mId = rs.getInt("mandante_id");
                    vId = rs.getInt("visitante_id");
                    dt = rs.getString("data_hora");
                    gm = rs.getInt("gols_m");
                    gv = rs.getInt("gols_v");
                    fin = rs.getInt("finalizada");
                }
            }
        }

        if (mId != null) {
            Time mandante = timeDAO.buscarPorId(mId);
            Time visitante = timeDAO.buscarPorId(vId);
            LocalDateTime dataHora = LocalDateTime.parse(dt, FMT);
            Partida p = new Partida(mandante, visitante, dataHora);
            p.setId(id);
            if (fin == 1) p.finalizarPartida(gm, gv);
            return p;
        }
        return null;
    }
}
