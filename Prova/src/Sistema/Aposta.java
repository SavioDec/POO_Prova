package Sistema;

import java.time.LocalDateTime;

public class Aposta {
    private Partida partida;
    private int prevMandante;
    private int prevVisitante;

    public Aposta(Partida partida, int prevMandante, int prevVisitante, LocalDateTime momentoAposta) {
        if (!partida.aceitaApostas(momentoAposta)) {
            throw new IllegalStateException("Apostas encerradas. O limite de 20 minutos antes da partida foi atingido.");
        }
        this.partida = partida;
        this.prevMandante = prevMandante;
        this.prevVisitante = prevVisitante;
    }

    public Partida getPartida() { return partida; }
    public int getPrevMandante() { return prevMandante; }
    public int getPrevVisitante() { return prevVisitante; }
}