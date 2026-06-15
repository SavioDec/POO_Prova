package Sistema;

import java.time.LocalDateTime;

public class Aposta {
    private int id;
    private Partida partida;
    private int prevMandante;
    private int prevVisitante;
    private LocalDateTime dataHora;

    public Aposta(Partida partida, int prevMandante, int prevVisitante, LocalDateTime momentoAposta) {
        if (momentoAposta != null && !partida.aceitaApostas(momentoAposta)) {
            throw new IllegalStateException("Apostas encerradas. A partida já começou ou foi finalizada.");
        }
        this.partida = partida;
        this.prevMandante = prevMandante;
        this.prevVisitante = prevVisitante;
        this.dataHora = momentoAposta;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public Partida getPartida() { return partida; }
    public int getPrevMandante() { return prevMandante; }
    public int getPrevVisitante() { return prevVisitante; }
}