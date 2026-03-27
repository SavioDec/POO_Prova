package Sistema;

import java.util.Objects;

public class Time {
    private String nome;


    public Time(){}

    public Time(String nome){
        this.nome = nome;
    }


    public String getNome() {
        return nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Time time = (Time) o;
        return Objects.equals(nome, time.nome); // Compara pelo nome do time
    }

    @Override
    public int hashCode() {
        return Objects.hash(nome);
    }

}
