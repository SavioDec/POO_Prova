package Sistema;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Campeonato {

    private static final int MAX_CLUBES = 8;


    private List<Time> clubes;

    public Campeonato() {
        this.clubes = new ArrayList<>();
    }

    public void adicionaTime(Time novoTime) {
        if (novoTime == null) {
            throw new IllegalArgumentException("O clube não pode ser nulo.");
        }

        if (this.clubes.contains(novoTime)) {
            throw new IllegalArgumentException("O clube '" + novoTime.getNome() + "' já está cadastrado neste campeonato.");
        }

        if (this.clubes.size() >= MAX_CLUBES) {
            throw new IllegalStateException("O campeonato já atingiu o limite máximo de " + MAX_CLUBES + " clubes.");
        }

        this.clubes.add(novoTime);
    }


    public List<Time> getClubes() {
        return Collections.unmodifiableList(this.clubes);
    }
}