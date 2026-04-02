package Sistema;

public abstract class Pessoa {
    protected String nome;

    public Pessoa(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio.");
        }
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }


    public abstract String getPermissaoSistema();
}