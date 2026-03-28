package Sistema;

import java.util.ArrayList;
import java.util.List;

public class GrupoCampeonato {
    private String nome; // Ex: "Grupo A"
    private List<Time> times;

    // Construtor
    public GrupoCampeonato(String nome) {
        this.nome = nome;
        this.times = new ArrayList<>();
    }


    public boolean adicionarTime(Time time) {
        if (this.times.size() < 4) {
            this.times.add(time);
            return true; // Retorna true se conseguiu adicionar
        } else {
            // Se tentar colocar o 5º time, o sistema barra!
            System.out.println("Erro: O " + this.nome + " já está cheio! Limite de 4 times atingido.");
            return false;
        }
    }

    // Getters para você conseguir pegar essas informações depois
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
