package Sistema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Participante extends Pessoa {
    private List<Aposta> apostas;
    private int pontuacaoTotal;

    public Participante(String nome) {
        super(nome);
        this.apostas = new ArrayList<>();
        this.pontuacaoTotal = 0;
    }

    @Override
    public String getPermissaoSistema() {
        return "Acesso Padrão - Registrar Apostas";
    }

    public void registrarAposta(Aposta aposta) {
        this.apostas.add(aposta);
    }

    public List<Aposta> getApostas() {
        return Collections.unmodifiableList(apostas);
    }

    public void adicionarPontos(int pontos) {
        this.pontuacaoTotal += pontos;
    }

    public int getPontuacaoTotal() {
        return pontuacaoTotal;
    }

    @Override
    public String toString() {
        return this.nome;
    }
}