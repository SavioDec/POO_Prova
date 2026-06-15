# FutBet Pro ⚽

Sistema de gerenciamento de apostas de futebol voltado para grupos de amigos, com foco em usabilidade, design moderno e acompanhamento em tempo real. Desenvolvido em Java com interface customizada (Soft Dark Theme) e persistência de dados em SQLite.

## Recursos e Funcionalidades

O FutBet Pro oferece as seguintes funcionalidades principais:

*   **Sistema de Acesso (Login/Cadastro):** Diferenciação entre perfis de Administradores (gerenciamento) e Participantes (apostas).
*   **Gestão de Times e Partidas (Admin):** O administrador cadastra as equipes, agenda os confrontos (com data e hora precisas) e lança os resultados (placares finais). Possui um recurso para gerar resultados aleatórios para facilitar testes e simulações.
*   **Dashboard e Notificações:** Tela inicial com resumo de performance (pontos e apostas), agenda dos próximos jogos e relógio integrado. Todas as ações do sistema possuem feedback visual instantâneo (toasts).
*   **Gestão de Apostas:** Os participantes registram seus palpites para cada partida, informando os gols do mandante e visitante. As apostas são permitidas até o exato momento de início do jogo.
*   **Grupos de Apostas:** Os usuários criam e ingressam em grupos fechados para competir com amigos específicos.
*   **Rankings Duplos e Dinâmicos:** A aba de classificação exibe, lado a lado, o Ranking Global (todos os participantes do sistema) e o Ranking do Grupo selecionado. As tabelas são atualizadas automaticamente.
*   **Sistema de Pontuação:**
    *   Acertar o resultado (vencedor ou empate) e o placar exato: **10 pontos**.
    *   Acertar apenas a tendência do resultado (quem ganhou ou se empatou), mas errar o placar exato: **5 pontos**.
    *   Errar ambos: **0 pontos**.

## Arquitetura e Interface

*   **Padrão MVC e DAO:** Lógica de negócios isolada da interface e da persistência de dados.
*   **Banco de Dados:** Utiliza SQLite nativo com o driver JDBC (`sqlite-jdbc`).
*   **Design System:** Interface construída inteiramente do zero usando a API `Graphics2D` do Java Swing. O sistema adota um "Soft Dark Theme" responsivo, com suporte a inputs de teclado (Enter), separação visual via paleta de cores (Slate/Azul) e ausência de bibliotecas gráficas externas pesadas.

## Como Executar

### Pré-requisitos
*   Java Development Kit (JDK) 25 ou superior.
*   Drivers localizados na pasta `/lib` (`sqlite-jdbc.jar`, `slf4j-api.jar`, `slf4j-simple.jar`).

### Compilação e Execução
Utilize o terminal/prompt de comando na raiz do projeto:

```bash
# Compilar o código
javac --release 25 -cp "lib/*" Prova/src/Main.java Prova/src/Sistema/*.java Prova/src/Sistema/UI/*.java Prova/src/Sistema/DAO/*.java Prova/src/Sistema/Database/*.java -d out/production/Prova

# Executar a aplicação
java -classpath out/production/Prova:lib/* Main
```

## Estrutura do Banco de Dados
O banco de dados SQLite (`banco.db`) é gerado automaticamente na primeira execução, estruturado nas seguintes tabelas:
- `usuarios`: Armazena participantes e administradores.
- `times`: Catálogo de equipes.
- `partidas`: Registra confrontos, datas e status (finalizada ou pendente).
- `apostas`: Palpites dos usuários atrelados às partidas.
- `grupos`: Registros de ligas fechadas.
- `usuarios_grupos`: Tabela de associação (N:M) entre participantes e grupos.
- `logs`: Registro de eventos críticos do sistema.