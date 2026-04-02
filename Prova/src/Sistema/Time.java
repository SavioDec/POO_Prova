package Sistema;

public class Time {
    private String nome;

    public Time(String nome) {
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome do time inválido.");
        this.nome = nome;
    }

    public String getNome() { return nome; }

    @Override
    public String toString() { return nome; }
}