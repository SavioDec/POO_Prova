package Sistema;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class SistemaGUI extends JFrame {

    private Campeonato campeonato;
    private Administrador admin;
    private RegraPontuacao calculadora;

    private List<GrupoAposta> grupos;
    private List<Participante> todosParticipantes;
    private static final int MAX_GRUPOS = 5;

    private DefaultComboBoxModel<Time> modelTimes;
    private DefaultComboBoxModel<Time> modelMandante;
    private DefaultComboBoxModel<Time> modelVisitante;

    private DefaultComboBoxModel<Partida> modelPartidas;
    private DefaultComboBoxModel<GrupoAposta> modelGrupos;
    private DefaultListModel<Participante> modelParticipantesGrupo;

    private JTextArea txtLog;

    public SistemaGUI() {
        aplicarCoresAltaVisibilidade();
        setTitle("Sistema de Apostas - Campeonato");
        setSize(1420, 924);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        inicializarSistema();
        inicializarGUI();
    }

    private void aplicarCoresAltaVisibilidade() {
        Color fundoPrimario = new Color(30, 30, 30);
        Color fundoSecundario = new Color(65, 65, 65);
        Color textoPrincipal = new Color(230, 230, 230);
        Color destaqueBotao = new Color(0, 120, 215);
        Color bordaInput = new Color(100, 100, 100);

        UIManager.put("Panel.background", fundoPrimario);
        UIManager.put("OptionPane.background", fundoPrimario);
        UIManager.put("OptionPane.messageForeground", textoPrincipal);
        UIManager.put("Label.foreground", textoPrincipal);

        UIManager.put("Button.background", destaqueBotao);
        UIManager.put("Button.foreground", Color.WHITE);


        UIManager.put("TextField.background", fundoSecundario);
        UIManager.put("TextField.foreground", textoPrincipal);
        UIManager.put("TextField.caretForeground", textoPrincipal);
        UIManager.put("TextField.border", BorderFactory.createLineBorder(bordaInput));

        UIManager.put("TextArea.background", fundoSecundario);
        UIManager.put("TextArea.foreground", textoPrincipal);
        UIManager.put("TextArea.caretForeground", textoPrincipal);
        UIManager.put("TextArea.border", BorderFactory.createLineBorder(bordaInput));

        UIManager.put("ComboBox.background", fundoSecundario);
        UIManager.put("ComboBox.foreground", textoPrincipal);
        UIManager.put("ComboBox.selectionBackground", destaqueBotao);
        UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        UIManager.put("ComboBox.border", BorderFactory.createLineBorder(bordaInput));

        UIManager.put("List.background", fundoSecundario);
        UIManager.put("List.foreground", textoPrincipal);
        UIManager.put("List.selectionBackground", destaqueBotao);
        UIManager.put("List.selectionForeground", Color.WHITE);
        UIManager.put("List.border", BorderFactory.createLineBorder(bordaInput));

        UIManager.put("TabbedPane.background", fundoPrimario);
        UIManager.put("TabbedPane.foreground", textoPrincipal);
        UIManager.put("TabbedPane.selected", fundoSecundario);
        UIManager.put("TabbedPane.contentAreaColor", fundoPrimario);

        UIManager.put("TitledBorder.titleColor", textoPrincipal);


        SwingUtilities.updateComponentTreeUI(this);
    }

    private void inicializarSistema() {
        admin = new Administrador("Administrador");
        calculadora = new CalculadoraPontos();
        campeonato = new Campeonato("Brasileirão");
        grupos = new ArrayList<>();
        todosParticipantes = new ArrayList<>();

        modelTimes = new DefaultComboBoxModel<>();
        modelMandante = new DefaultComboBoxModel<>();
        modelVisitante = new DefaultComboBoxModel<>();

        modelPartidas = new DefaultComboBoxModel<>();
        modelGrupos = new DefaultComboBoxModel<>();
        modelParticipantesGrupo = new DefaultListModel<>();
    }

    private void inicializarGUI() {
        JTabbedPane abas = new JTabbedPane();
        abas.add("Times", criarPainelTimes());
        abas.add("Partidas", criarPainelPartidas());
        abas.add("Grupos", criarPainelGrupos());
        abas.add("Apostas", criarPainelApostas());
        abas.add("Administrador", criarPainelAdmin());
        abas.add("Classificação", criarPainelClassificacao());

        txtLog = new JTextArea(10, 40);
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Monospaced", Font.PLAIN, 30));
        JScrollPane scrollLog = new JScrollPane(txtLog);
        scrollLog.setBorder(new TitledBorder("Log do Sistema"));

        setLayout(new BorderLayout());
        add(abas, BorderLayout.CENTER);
        add(scrollLog, BorderLayout.SOUTH);
    }

    private JPanel criarPainelTimes() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new FlowLayout());
        JTextField txtNomeTime = new JTextField(20);
        JButton btnCadastrar = new JButton("Cadastrar Time");
        form.add(new JLabel("Nome do Time:"));
        form.add(txtNomeTime);
        form.add(btnCadastrar);

        JList<Time> listTimes = new JList<>(modelTimes);
        listTimes.setVisibleRowCount(6);
        JScrollPane scrollTimes = new JScrollPane(listTimes);
        scrollTimes.setBorder(new TitledBorder("Times Cadastrados"));

        btnCadastrar.addActionListener(e -> {
            String nome = txtNomeTime.getText().trim();
            if (nome.isEmpty()) {
                mostrarErro("Nome do time não pode ser vazio.");
                return;
            }
            try {
                Time time = new Time(nome);
                campeonato.adicionarTime(time);

                modelTimes.addElement(time);
                modelMandante.addElement(time);
                modelVisitante.addElement(time);

                log("Time cadastrado: " + nome);
                txtNomeTime.setText("");
            } catch (Exception ex) {
                mostrarErro(ex.getMessage());
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(scrollTimes, BorderLayout.CENTER);
        return panel;
    }

    private JPanel criarPainelPartidas() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(3, 2, 5, 5));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));

        JComboBox<Time> cbMandante = new JComboBox<>(modelMandante);
        JComboBox<Time> cbVisitante = new JComboBox<>(modelVisitante);
        JTextField txtDataHora = new JTextField("", 20);
        JButton btnCriar = new JButton("Criar Partida");

        form.add(new JLabel("Mandante:"));
        form.add(cbMandante);
        form.add(new JLabel("Visitante:"));
        form.add(cbVisitante);
        form.add(new JLabel("Data/Hora (dd/MM/yyyy HH:mm):"));
        form.add(txtDataHora);

        JList<Partida> listPartidas = new JList<>(modelPartidas);
        listPartidas.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Partida p = (Partida) value;
                String status = p.isFinalizada() ? "[FINALIZADA] " : "";
                String texto = status + p.getMandante().getNome() + " x " + p.getVisitante().getNome() +
                        " - " + p.getDataHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                return super.getListCellRendererComponent(list, texto, index, isSelected, cellHasFocus);
            }
        });
        JScrollPane scrollPartidas = new JScrollPane(listPartidas);
        scrollPartidas.setBorder(new TitledBorder("Partidas do Campeonato"));

        btnCriar.addActionListener(e -> {
            Time mandante = (Time) cbMandante.getSelectedItem();
            Time visitante = (Time) cbVisitante.getSelectedItem();
            if (mandante == null || visitante == null) {
                mostrarErro("Selecione mandante e visitante.");
                return;
            }
            if (mandante.equals(visitante)) {
                mostrarErro("Mandante e visitante devem ser diferentes.");
                return;
            }
            try {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                LocalDateTime dataHora = LocalDateTime.parse(txtDataHora.getText(), fmt);
                Partida partida = new Partida(mandante, visitante, dataHora);
                campeonato.registrarPartida(partida);
                modelPartidas.addElement(partida);
                log("Partida criada: " + partida);
                txtDataHora.setText("");
            } catch (Exception ex) {
                mostrarErro("Erro ao criar partida: " + ex.getMessage());
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(form, BorderLayout.CENTER);
        topPanel.add(btnCriar, BorderLayout.SOUTH);
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPartidas, BorderLayout.CENTER);
        return panel;
    }

    private JPanel criarPainelGrupos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel criarGrupoPanel = new JPanel(new FlowLayout());
        JTextField txtNomeGrupo = new JTextField(15);
        JTextField txtCriador = new JTextField(10);
        JButton btnCriarGrupo = new JButton("Criar Grupo");
        criarGrupoPanel.add(new JLabel("Nome do Grupo:"));
        criarGrupoPanel.add(txtNomeGrupo);
        criarGrupoPanel.add(new JLabel("Criador (nome):"));
        criarGrupoPanel.add(txtCriador);
        criarGrupoPanel.add(btnCriarGrupo);

        JPanel addParticipantePanel = new JPanel(new FlowLayout());
        JComboBox<GrupoAposta> cbGrupos = new JComboBox<>(modelGrupos);
        JTextField txtNovoParticipante = new JTextField(10);
        JButton btnAddParticipante = new JButton("Adicionar Participante");
        addParticipantePanel.add(new JLabel("Grupo:"));
        addParticipantePanel.add(cbGrupos);
        addParticipantePanel.add(new JLabel("Nome do Participante:"));
        addParticipantePanel.add(txtNovoParticipante);
        addParticipantePanel.add(btnAddParticipante);

        JList<Participante> listParticipantes = new JList<>(modelParticipantesGrupo);
        JScrollPane scrollParticipantes = new JScrollPane(listParticipantes);
        scrollParticipantes.setBorder(new TitledBorder("Participantes do Grupo Selecionado"));

        cbGrupos.addActionListener(e -> {
            GrupoAposta grupo = (GrupoAposta) cbGrupos.getSelectedItem();
            if (grupo != null) {
                modelParticipantesGrupo.clear();
                for (Participante p : grupo.getParticipantes()) {
                    modelParticipantesGrupo.addElement(p);
                }
            }
        });

        btnCriarGrupo.addActionListener(e -> {
            if (grupos.size() >= MAX_GRUPOS) {
                mostrarErro("Limite máximo de " + MAX_GRUPOS + " grupos atingido.");
                return;
            }
            String nomeGrupo = txtNomeGrupo.getText().trim();
            String nomeCriador = txtCriador.getText().trim();
            if (nomeGrupo.isEmpty() || nomeCriador.isEmpty()) {
                mostrarErro("Informe o nome do grupo e do criador.");
                return;
            }

            boolean grupoJaExiste = grupos.stream().anyMatch(g -> g.getNome().equalsIgnoreCase(nomeGrupo));
            if (grupoJaExiste) {
                mostrarErro("Já existe um grupo com o nome '" + nomeGrupo + "'.");
                return;
            }

            try {
                Participante criador = new Participante(nomeCriador);
                todosParticipantes.add(criador);

                GrupoAposta grupo = new GrupoAposta(nomeGrupo, criador);
                grupos.add(grupo);
                modelGrupos.addElement(grupo);
                log("Grupo '" + nomeGrupo + "' criado por " + nomeCriador);
                txtNomeGrupo.setText("");
                txtCriador.setText("");
            } catch (Exception ex) {
                mostrarErro(ex.getMessage());
            }
        });

        btnAddParticipante.addActionListener(e -> {
            GrupoAposta grupo = (GrupoAposta) cbGrupos.getSelectedItem();
            if (grupo == null) {
                mostrarErro("Selecione um grupo.");
                return;
            }
            String nomeParticipante = txtNovoParticipante.getText().trim();
            if (nomeParticipante.isEmpty()) {
                mostrarErro("Informe o nome do participante.");
                return;
            }
            if (participanteJaEstaEmGrupo(grupo, nomeParticipante)) {
                mostrarErro("Este participante já está neste grupo.");
                return;
            }

            try {
                Participante novo = new Participante(nomeParticipante);
                todosParticipantes.add(novo);

                grupo.adicionarParticipante(novo);
                modelParticipantesGrupo.addElement(novo);
                log("Participante '" + nomeParticipante + "' adicionado ao grupo '" + grupo.getNome() + "'");
                txtNovoParticipante.setText("");
            } catch (Exception ex) {
                mostrarErro(ex.getMessage());
            }
        });

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.add(criarGrupoPanel);
        top.add(addParticipantePanel);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollParticipantes, BorderLayout.CENTER);
        return panel;
    }

    private JPanel criarPainelApostas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel form = new JPanel(new GridLayout(5, 2, 5, 5));
        form.setBorder(new EmptyBorder(10, 10, 10, 10));

        JComboBox<GrupoAposta> cbGruposAposta = new JComboBox<>(modelGrupos);
        JComboBox<Participante> cbParticipantes = new JComboBox<>();
        JComboBox<Partida> cbPartidas = new JComboBox<>(modelPartidas);
        JTextField txtGolsM = new JTextField(5);
        JTextField txtGolsV = new JTextField(5);
        JButton btnApostar = new JButton("Registrar Aposta");

        cbGruposAposta.addActionListener(e -> {
            GrupoAposta grupoSelecionado = (GrupoAposta) cbGruposAposta.getSelectedItem();
            cbParticipantes.removeAllItems();
            if (grupoSelecionado != null) {
                for (Participante p : grupoSelecionado.getParticipantes()) {
                    cbParticipantes.addItem(p);
                }
            }
        });

        form.add(new JLabel("Grupo:"));
        form.add(cbGruposAposta);
        form.add(new JLabel("Participante:"));
        form.add(cbParticipantes);
        form.add(new JLabel("Partida:"));
        form.add(cbPartidas);
        form.add(new JLabel("Placar Mandante:"));
        form.add(txtGolsM);
        form.add(new JLabel("Placar Visitante:"));
        form.add(txtGolsV);

        btnApostar.addActionListener(e -> {
            Participante participante = (Participante) cbParticipantes.getSelectedItem();
            Partida partida = (Partida) cbPartidas.getSelectedItem();

            if (participante == null || partida == null) {
                mostrarErro("Selecione participante e partida.");
                return;
            }
            if (partida.isFinalizada()) {
                mostrarErro("Esta partida já foi finalizada. Não é possível apostar.");
                return;
            }
            if (!partida.aceitaApostas(LocalDateTime.now())) {
                mostrarErro("O prazo para apostas nesta partida já expirou (máximo 20 minutos antes).");
                return;
            }
            int golsM, golsV;
            try {
                golsM = Integer.parseInt(txtGolsM.getText());
                golsV = Integer.parseInt(txtGolsV.getText());
                if (golsM < 0 || golsV < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                mostrarErro("Informe placares válidos (números inteiros não negativos).");
                return;
            }

            Aposta apostaExistente = null;
            for (Aposta a : participante.getApostas()) {
                if (a.getPartida().equals(partida)) {
                    apostaExistente = a;
                    break;
                }
            }
            if (apostaExistente != null) {
                int resp = JOptionPane.showConfirmDialog(this,
                        "Este participante já possui aposta para esta partida.",
                        "Aposta existente", JOptionPane.YES_NO_OPTION);
                if (resp != JOptionPane.YES_OPTION) return;
            }

            try {
                Aposta nova = new Aposta(partida, golsM, golsV, LocalDateTime.now());
                participante.registrarAposta(nova);
                log("Aposta registrada: " + participante.getNome() + " - " + partida + " -> " + golsM + "x" + golsV);
                txtGolsM.setText("");
                txtGolsV.setText("");
            } catch (Exception ex) {
                mostrarErro("Erro ao registrar aposta: " + ex.getMessage());
            }
        });

        panel.add(form, BorderLayout.CENTER);
        panel.add(btnApostar, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarPainelAdmin() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel resultPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        resultPanel.setBorder(new TitledBorder("Registrar Resultado Real"));

        JComboBox<Partida> cbPartidas = new JComboBox<>(modelPartidas);
        JTextField txtGolsM = new JTextField(5);
        JTextField txtGolsV = new JTextField(5);
        JButton btnRegistrar = new JButton("Registrar Resultado");

        resultPanel.add(new JLabel("Partida:"));
        resultPanel.add(cbPartidas);
        resultPanel.add(new JLabel("Gols Mandante:"));
        resultPanel.add(txtGolsM);
        resultPanel.add(new JLabel("Gols Visitante:"));
        resultPanel.add(txtGolsV);

        btnRegistrar.addActionListener(e -> {
            Partida partida = (Partida) cbPartidas.getSelectedItem();
            if (partida == null) {
                mostrarErro("Selecione uma partida.");
                return;
            }
            if (partida.isFinalizada()) {
                int resp = JOptionPane.showConfirmDialog(this,
                        "Partida já finalizada. Alterar o resultado pode afetar pontuações. Continuar?",
                        "Alterar resultado", JOptionPane.YES_NO_OPTION);
                if (resp != JOptionPane.YES_OPTION) return;
            }
            int golsM, golsV;
            try {
                golsM = Integer.parseInt(txtGolsM.getText());
                golsV = Integer.parseInt(txtGolsV.getText());
                if (golsM < 0 || golsV < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                mostrarErro("Informe placares válidos (números inteiros não negativos).");
                return;
            }
            try {
                admin.registrarResultadoPartida(partida, golsM, golsV);
                log("Resultado registrado: " + partida + " -> " + golsM + "x" + golsV);
                cbPartidas.repaint();
                txtGolsM.setText("");
                txtGolsV.setText("");
            } catch (Exception ex) {
                mostrarErro("Erro ao registrar resultado: " + ex.getMessage());
            }
        });

        panel.add(resultPanel, BorderLayout.NORTH);
        panel.add(btnRegistrar, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel criarPainelClassificacao() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel top = new JPanel(new FlowLayout());
        JComboBox<GrupoAposta> cbGrupos = new JComboBox<>(modelGrupos);
        JButton btnMostrar = new JButton("Mostrar Classificação");
        top.add(new JLabel("Selecione o Grupo:"));
        top.add(cbGrupos);
        top.add(btnMostrar);

        JTextArea txtRanking = new JTextArea(15, 50);
        txtRanking.setEditable(false);
        txtRanking.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane scrollRanking = new JScrollPane(txtRanking);
        scrollRanking.setBorder(new TitledBorder("Classificação"));

        btnMostrar.addActionListener(e -> {
            GrupoAposta grupo = (GrupoAposta) cbGrupos.getSelectedItem();
            if (grupo == null) {
                mostrarErro("Selecione um grupo.");
                return;
            }
            List<Participante> participantes = grupo.getParticipantes();
            if (participantes.isEmpty()) {
                txtRanking.setText("Nenhum participante neste grupo.");
                return;
            }

            Map<Participante, Integer> pontosMap = new HashMap<>();
            for (Participante p : participantes) {
                int total = 0;
                for (Aposta a : p.getApostas()) {
                    total += calculadora.calcularPontos(a);
                }
                pontosMap.put(p, total);
            }

            List<Map.Entry<Participante, Integer>> sorted = new ArrayList<>(pontosMap.entrySet());
            sorted.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

            StringBuilder sb = new StringBuilder();
            sb.append("Ranking do Grupo '").append(grupo.getNome()).append("'\n");
            sb.append("--------------------------------\n");
            int pos = 1;
            for (Map.Entry<Participante, Integer> entry : sorted) {
                sb.append(pos++).append(". ")
                        .append(entry.getKey().getNome()).append(" - ")
                        .append(entry.getValue()).append(" ponto(s)\n");
            }
            txtRanking.setText(sb.toString());
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(scrollRanking, BorderLayout.CENTER);
        return panel;
    }

    private void log(String msg) {
        txtLog.append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " - " + msg + "\n");
        txtLog.setCaretPosition(txtLog.getDocument().getLength());
    }

    private void mostrarErro(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
        log("ERRO: " + msg);
    }

    private boolean participanteJaEstaEmGrupo(GrupoAposta grupo, String nome) {
        for (Participante p : grupo.getParticipantes()) {
            if (p.getNome().equalsIgnoreCase(nome)) {
                return true;
            }
        }
        return false;
    }
}