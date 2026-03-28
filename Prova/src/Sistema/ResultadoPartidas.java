package Sistema;

public class ResultadoPartidas {
    private int golsMandante;
    private int golsVisitante;

    public ResultadoPartidas (int golsMandante, int golsVisitante) {
        this.golsMandante = golsMandante;
        this.golsVisitante =  golsVisitante;
    }

    public String getVeredito() {
        if (golsMandante > golsVisitante) {
            return "vitoria mandante";
        } else if (golsVisitante > golsMandante) {
            return "vitoria visitante";
        } else {
            return "empate";
        }
    }

    public int getGolsMandante(){
        return golsMandante;
    }
    public int getGolsVisitante(){
        return golsVisitante;

    }
}