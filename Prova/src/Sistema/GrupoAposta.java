package Sistema;

import java.util.Objects;

public class GrupoAposta {

    private String nome;
    private Usuario criador; // Renomeado para dar contexto ao papel do usuário

    public GrupoAposta(String nome, Usuario criador) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do grupo não pode ser vazio.");
        }
        if (criador == null) {
            throw new IllegalArgumentException("Um grupo precisa de um usuário criador.");
        }
        this.nome = nome;
        this.criador = criador;
    }

    public String getNome() {
        return nome;
    }


    @Override
    public boolean equals(Object o){
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GrupoAposta that = (GrupoAposta) o;
        return Objects.equals(nome, that.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }




}
