package Sistema;

public class Time {
    private int id;
    private String nome;

    public Time(String nome) {
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome do time inválido.");
        this.nome = nome;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }

    @Override
    public String toString() { return nome; }
}