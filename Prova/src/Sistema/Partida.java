package Sistema;

import java.time.LocalDateTime;

public class Partida {
    private Time mandante;
    private Time visitante;
    private LocalDateTime dataHoraInicio;
    private int golsMandante;
    private int golsVisitante;
    private boolean finalizada;

    public Partida(Time mandante, Time visitante, LocalDateTime dataHoraInicio) {
        this.mandante = mandante;
        this.visitante = visitante;
        this.dataHoraInicio = dataHoraInicio;
        this.finalizada = false;
    }

    public void finalizarPartida(int golsMandante, int golsVisitante) {
        if (golsMandante < 0 || golsVisitante < 0) throw new IllegalArgumentException("Placar inválido.");
        this.golsMandante = golsMandante;
        this.golsVisitante = golsVisitante;
        this.finalizada = true;
    }

    public boolean aceitaApostas(LocalDateTime momentoAtual) {

        return momentoAtual.isBefore(dataHoraInicio.minusMinutes(20));
    }

    public Time getMandante() { return mandante; }
    public Time getVisitante() { return visitante; }
    public LocalDateTime getDataHoraInicio() { return dataHoraInicio; }
    public int getGolsMandante() { return golsMandante; }
    public int getGolsVisitante() { return golsVisitante; }
    public boolean isFinalizada() { return finalizada; }

    @Override
    public String toString() {
        return mandante.getNome() + " x " + visitante.getNome();
    }
}