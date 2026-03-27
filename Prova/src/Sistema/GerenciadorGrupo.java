package Sistema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GerenciadorGrupo {

    private static final int MAX_GRUPOS = 5;

    private List<GrupoAposta> gruposAtivos;

    public GerenciadorGrupo() {

        this.gruposAtivos = new ArrayList<>();
    }


    public void registrarGrupo(GrupoAposta novoGrupo) {
        if (novoGrupo == null) {
            throw new IllegalArgumentException("O grupo não pode ser nulo.");
        }

        if (this.gruposAtivos.contains(novoGrupo)) {
            throw new IllegalArgumentException("O grupo '" + novoGrupo.getNome() + "' já está cadastrado.");
        }

        if (this.gruposAtivos.size() >= MAX_GRUPOS) {
            throw new IllegalStateException("O limite máximo de " + MAX_GRUPOS + " grupos já foi alcançado no sistema.");
        }

        this.gruposAtivos.add(novoGrupo);
    }


    public List<GrupoAposta> getGrupos() {
        return Collections.unmodifiableList(this.gruposAtivos);
    }
}