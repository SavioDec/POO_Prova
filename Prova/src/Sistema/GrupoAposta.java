package Sistema;

import java.util.ArrayList;
import java.util.List;

public class GrupoAposta {

    private String nome;
    private Participante criador;
    private List<Participante> participantes;

    public GrupoAposta(String nome, Participante criador) {
        this.nome = nome;
        this.criador = criador;
        this.participantes = new ArrayList<>();
        this.participantes.add(criador);
    }

    public void adicionarParticipante(Participante participante) {
        if (!participantes.contains(participante)) {
            participantes.add(participante);
        }
    }

    public List<Participante> getParticipantes() {
        return participantes;
    }

    public String getNome() {
        return nome;
    }

    public Participante getCriador() {
        return criador;
    }

    @Override
    public String toString() {
        return nome;
    }
}