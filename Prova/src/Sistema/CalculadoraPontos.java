package Sistema;

public class CalculadoraPontos implements RegraPontuacao {

    @Override
    public int calcularPontos(Aposta aposta) {
        Partida p = aposta.getPartida();
        if (!p.isFinalizada()) return 0;

        int golsRealM = p.getGolsMandante();
        int golsRealV = p.getGolsVisitante();
        int prevM = aposta.getPrevMandante();
        int prevV = aposta.getPrevVisitante();


        if (golsRealM == prevM && golsRealV == prevV) {
            return 10;
        }


        int saldoReal = golsRealM - golsRealV;
        int saldoAposta = prevM - prevV;

        boolean acertouMandante = (saldoReal > 0 && saldoAposta > 0);
        boolean acertouVisitante = (saldoReal < 0 && saldoAposta < 0);
        boolean acertouEmpate = (saldoReal == 0 && saldoAposta == 0);

        if (acertouMandante || acertouVisitante || acertouEmpate) {
            return 5;
        }

        return 0;
    }
}