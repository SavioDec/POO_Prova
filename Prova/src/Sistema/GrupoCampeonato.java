package Sistema;

import java.util.ArrayList;
import java.util.List;

public class GrupoCampeonato {
    private String nome;
    private List<Time> times;


    public GrupoCampeonato(String nome) {
        this.nome = nome;
        this.times = new ArrayList<>();
    }


    public boolean adicionarTime(Time time) {
        if (this.times.size() < 4) {
            this.times.add(time);
            return true;
        } else {

            System.out.println("Erro: O " + this.nome + " já está cheio! Limite de 4 times atingido.");
            return false;
        }
    }


    public String getNome() {
        return nome;
    }

    public List<Time> getTimes() {
        return times;
    }


    public void exibirGrupo() {
        System.out.println("--- " + this.nome + " ---");
        for (Time t : times) {
            System.out.println("- " + t.getNome());
        }
        System.out.println("-----------------");
    }
}
