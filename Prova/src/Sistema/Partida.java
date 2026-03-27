package Sistema;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Partida {

    private String clubeMandante;
    private String clubeVisitante;
    private LocalDateTime dataHorarioJogo;

    public Partida(String clubeMandante, String clubeVisitante, LocalDateTime dataHorarioJogo) {
        this.clubeMandante = clubeMandante;
        this.clubeVisitante = clubeVisitante;
        this.dataHorarioJogo = dataHorarioJogo;
    }

    public String getClubeMandante() {
        return clubeMandante;
    }

    public String getClubeVisitante() {
        return clubeVisitante;
    }

    public LocalDateTime getDataHorarioJogo() {
        return dataHorarioJogo;
    }

    public String getDataHorarioFormatado() {

        if (this.dataHorarioJogo == null) {
            return "Data não definida";
        }

        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return this.dataHorarioJogo.format(formatador);
    }

    public void setClubeMandante(String clubeMandante) {
        this.clubeMandante = clubeMandante;
    }

    public void setClubeVisitante(String clubeVisitante) {
        this.clubeVisitante = clubeVisitante;
    }

    public void setDataHorarioJogo(LocalDateTime dataHorarioJogo) {
        this.dataHorarioJogo = dataHorarioJogo;
    }

    @Override
    public String toString() {
        return "Partida: " + clubeMandante + " x " + clubeVisitante +
                " | marcada para: " + getDataHorarioFormatado();
    }
}