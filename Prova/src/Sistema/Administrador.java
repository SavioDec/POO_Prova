package Sistema;

public class Administrador extends Pessoa {

    public Administrador(String nome) {
        super(nome);
    }

    @Override
    public String getPermissaoSistema() {
        return "Acesso Total - Registrar Resultados Reais";
    }

    public void registrarResultadoPartida(Partida partida, int golsMandante, int golsVisitante) {
        partida.finalizarPartida(golsMandante, golsVisitante);
    }
}