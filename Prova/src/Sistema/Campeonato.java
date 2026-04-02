package Sistema;

import java.util.ArrayList;
import java.util.List;

public class Campeonato {
    private String nome;
    private List<Time> clubes;
    private List<Partida> partidas;
    private static final int MAX_CLUBES = 8;

    public Campeonato(String nome) {
        this.nome = nome;
        this.clubes = new ArrayList<>();
        this.partidas = new ArrayList<>();
    }

    public void adicionarTime(Time time) {
        if (clubes.size() >= MAX_CLUBES) throw new IllegalStateException("Limite de 8 clubes atingido.");
        if (!clubes.contains(time)) clubes.add(time);
    }

    public void registrarPartida(Partida partida) {
        this.partidas.add(partida);
    }

    public List<Partida> getPartidas() { return partidas; }
}