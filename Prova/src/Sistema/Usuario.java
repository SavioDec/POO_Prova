package Sistema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Usuario {

    private String nome;
    private GrupoAposta grupo;
    private List<Aposta> históricoApostas;


    public Usuario(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do usuário não pode ser vazio.");
        }
        this.nome = nome;
        this.históricoApostas = new ArrayList<>();
    }


    public String getNome() {
        return nome;
    }


    public void vincularGrupo(GrupoAposta novoGrupo) {
        if (novoGrupo == null) {
            throw new IllegalArgumentException("O grupo não pode ser nulo.");
        }
        this.grupo = novoGrupo;
    }

    public void sairDoGrupo() {
        this.grupo = null;
    }

    public GrupoAposta getGrupo() {
        return grupo;
    }


    public void registrarAposta(Aposta novaAposta) {
        if (novaAposta == null) {
            throw new IllegalArgumentException("A aposta não pode ser nula.");
        }
        this.históricoApostas.add(novaAposta);
    }


    public List<Aposta> getApostas() {
        return Collections.unmodifiableList(this.históricoApostas);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(nome, usuario.nome);

    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }
}