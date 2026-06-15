package Sistema.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:banco.db";

    /**
     * Retorna uma NOVA conexão com o banco de dados.
     * Importante para evitar conflitos de ponteiro no SQLite com operações aninhadas.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver SQLite não encontrado.", e);
        }
    }

    public static void inicializarBanco() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            String path = new java.io.File("banco.db").getAbsolutePath();
            System.out.println("Banco de Dados inicializado em: " + path);
            
            // Tabela de Usuários (Login)
            stmt.execute("CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nome TEXT NOT NULL UNIQUE," +
                    "senha TEXT NOT NULL," +
                    "role TEXT NOT NULL" +
                    ")");

            // Tabela de Times
            stmt.execute("CREATE TABLE IF NOT EXISTS times (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nome TEXT NOT NULL UNIQUE" +
                    ")");

            // Tabela de Partidas
            stmt.execute("CREATE TABLE IF NOT EXISTS partidas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "mandante_id INTEGER NOT NULL," +
                    "visitante_id INTEGER NOT NULL," +
                    "data_hora TEXT NOT NULL," +
                    "gols_m INTEGER DEFAULT 0," +
                    "gols_v INTEGER DEFAULT 0," +
                    "finalizada INTEGER DEFAULT 0," +
                    "FOREIGN KEY (mandante_id) REFERENCES times(id)," +
                    "FOREIGN KEY (visitante_id) REFERENCES times(id)" +
                    ")");

            // Tabela de Grupos
            stmt.execute("CREATE TABLE IF NOT EXISTS grupos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nome TEXT NOT NULL UNIQUE," +
                    "criador_id INTEGER NOT NULL," +
                    "FOREIGN KEY (criador_id) REFERENCES usuarios(id)" +
                    ")");

            // Tabela de Relacionamento Grupo-Usuário
            stmt.execute("CREATE TABLE IF NOT EXISTS grupo_usuarios (" +
                    "grupo_id INTEGER NOT NULL," +
                    "usuario_id INTEGER NOT NULL," +
                    "PRIMARY KEY (grupo_id, usuario_id)," +
                    "FOREIGN KEY (grupo_id) REFERENCES grupos(id)," +
                    "FOREIGN KEY (usuario_id) REFERENCES usuarios(id)" +
                    ")");

            // Tabela de Apostas
            stmt.execute("CREATE TABLE IF NOT EXISTS apostas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "usuario_id INTEGER NOT NULL," +
                    "partida_id INTEGER NOT NULL," +
                    "gols_m INTEGER NOT NULL," +
                    "gols_v INTEGER NOT NULL," +
                    "data_hora TEXT NOT NULL," +
                    "FOREIGN KEY (usuario_id) REFERENCES usuarios(id)," +
                    "FOREIGN KEY (partida_id) REFERENCES partidas(id)" +
                    ")");

            // Inserir usuário Admin padrão se não existir
            stmt.execute("INSERT OR IGNORE INTO usuarios (nome, senha, role) VALUES ('admin', 'admin', 'ADMIN')");
            
        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco: " + e.getMessage());
        }
    }
}
