package Sistema;

import Sistema.DAO.*;
import Sistema.UI.LoginGUI;
import Sistema.UI.UIUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SistemaGUI extends JFrame {
    private Pessoa user;
    private RegraPontuacao calc = new CalculadoraPontos();
    private TimeDAO tDAO = new TimeDAO();
    private PartidaDAO pDAO = new PartidaDAO();
    private GrupoDAO gDAO = new GrupoDAO();
    private ApostaDAO aDAO = new ApostaDAO();

    private DefaultComboBoxModel<Time> mT = new DefaultComboBoxModel<>();
    private DefaultComboBoxModel<Time> mT_V = new DefaultComboBoxModel<>();
    private DefaultComboBoxModel<Partida> mP = new DefaultComboBoxModel<>();
    private DefaultComboBoxModel<GrupoAposta> mG = new DefaultComboBoxModel<>();
    private DefaultListModel<Participante> mL = new DefaultListModel<>();

    private JTextArea txtDashboardStats;
    private JLabel lblClock;
    private JPanel activeNavIndicator;
    private String currentCard = "home";

    public SistemaGUI(Pessoa user) {
        this.user = user;
        setTitle("FutBet Pro ⚽");
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(UIUtils.BG);
        
        loadData();
        initGUI();
        startClock();
    }

    private void startClock() {
        new Timer(1000, e -> {
            if (lblClock != null) lblClock.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }).start();
    }

    private void loadData() {
        try {
            mT.removeAllElements(); mT_V.removeAllElements();
            mP.removeAllElements(); mG.removeAllElements();
            List<Time> all = tDAO.listarTodos();
            all.forEach(t -> { mT.addElement(t); mT_V.addElement(t); });
            pDAO.listarTodas().forEach(mP::addElement);
            gDAO.listarTodos().forEach(mG::addElement);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void initGUI() {
        setLayout(new BorderLayout());

        // Sidebar (Minimalista)
        JPanel sidebar = new JPanel();
        sidebar.setBackground(UIUtils.SURFACE);
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, UIUtils.BORDER));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 30));
        header.setOpaque(false);
        header.add(UIUtils.createLabel("⚽ FUTBET PRO", 18, true));
        sidebar.add(header);

        lblClock = UIUtils.createLabel("--:--:--", 12, false);
        JPanel clockPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        clockPanel.setOpaque(false);
        clockPanel.add(lblClock);
        sidebar.add(clockPanel);
        sidebar.add(Box.createVerticalStrut(40));

        CardLayout cardLayout = new CardLayout();
        JPanel mainContent = new JPanel(cardLayout);
        mainContent.setOpaque(false);

        addNavButton(sidebar, "Dashboard", "home", mainContent, cardLayout);
        addNavButton(sidebar, "Partidas", "games", mainContent, cardLayout);
        addNavButton(sidebar, "Apostar", "bet", mainContent, cardLayout);
        addNavButton(sidebar, "Grupos", "groups", mainContent, cardLayout);
        addNavButton(sidebar, "Ranking", "rank", mainContent, cardLayout);

        if (user instanceof Administrador) {
            sidebar.add(Box.createVerticalStrut(20));
            JPanel admLabel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 10));
            admLabel.setOpaque(false);
            admLabel.add(UIUtils.createLabel("ADMINISTRAÇÃO", 10, true));
            sidebar.add(admLabel);
            addNavButton(sidebar, "🛡️ Times", "adm_t", mainContent, cardLayout);
            addNavButton(sidebar, "⚡ Jogos", "adm_p", mainContent, cardLayout);
            addNavButton(sidebar, "🏆 Resultados", "adm_r", mainContent, cardLayout);
        }

        sidebar.add(Box.createVerticalGlue());
        JButton btnLogout = UIUtils.createButton("Sair da Conta", false, true);
        btnLogout.addActionListener(e -> { new LoginGUI().setVisible(true); dispose(); });
        JPanel logoutContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 30));
        logoutContainer.setOpaque(false); logoutContainer.add(btnLogout);
        sidebar.add(logoutContainer);

        mainContent.add(panelDashboard(), "home");
        mainContent.add(panelPartidas(), "games");
        mainContent.add(panelApostas(), "bet");
        mainContent.add(panelGrupos(), "groups");
        mainContent.add(panelRanking(), "rank");
        if (user instanceof Administrador) {
            mainContent.add(panelAdminTimes(), "adm_t");
            mainContent.add(panelAdminPartidas(), "adm_p");
            mainContent.add(panelAdminResultados(), "adm_r");
        }

        add(sidebar, BorderLayout.WEST);
        add(mainContent, BorderLayout.CENTER);
    }

    private void addNavButton(JPanel side, String text, String cardName, JPanel main, CardLayout cl) {
        JButton b = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                if (currentCard.equals(cardName)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(UIUtils.ACCENT);
                    g2.fillRect(0, 0, 4, getHeight());
                    g2.dispose();
                }
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("Inter", Font.PLAIN, 14));
        b.setForeground(currentCard.equals(cardName) ? UIUtils.ACCENT : UIUtils.TEXT);
        b.setFocusPainted(false); b.setContentAreaFilled(false); b.setBorderPainted(false);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMargin(new Insets(10, 25, 10, 10));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(240, 45));
        
        b.addActionListener(e -> {
            currentCard = cardName;
            cl.show(main, cardName);
            side.repaint();
            if (cardName.equals("home") || cardName.equals("rank")) loadData();
        });
        side.add(b);
    }

    private JPanel panelDashboard() {
        JPanel p = createBasePanel();
        
        JPanel welcome = new JPanel(new BorderLayout());
        welcome.setOpaque(false);
        welcome.add(UIUtils.createLabel("Bom dia, " + user.getNome(), 26, true), BorderLayout.NORTH);
        welcome.add(UIUtils.createLabel("Acompanhe suas estatísticas e próximas partidas.", 14, false), BorderLayout.CENTER);
        
        JPanel grid = new JPanel(new GridLayout(1, 2, 30, 0));
        grid.setOpaque(false);

        JPanel statsCard = UIUtils.createCard();
        statsCard.setLayout(new BoxLayout(statsCard, BoxLayout.Y_AXIS));
        statsCard.add(UIUtils.createLabel("SUA PERFORMANCE", 11, true));
        statsCard.add(Box.createVerticalStrut(20));
        txtDashboardStats = new JTextArea();
        txtDashboardStats.setEditable(false); txtDashboardStats.setOpaque(false);
        txtDashboardStats.setFont(new Font("Inter", Font.BOLD, 18));
        txtDashboardStats.setForeground(UIUtils.TEXT);
        statsCard.add(txtDashboardStats);
        atualizarStats(txtDashboardStats);

        JPanel nextCard = UIUtils.createCard();
        nextCard.setLayout(new BorderLayout());
        nextCard.add(UIUtils.createLabel("AGENDA DE JOGOS", 11, true), BorderLayout.NORTH);
        DefaultListModel<Partida> mN = new DefaultListModel<>();
        JList<Partida> listN = new JList<>(mN);
        listN.setOpaque(false);
        listN.setBackground(new Color(0,0,0,0));
        listN.setForeground(UIUtils.TEXT);
        listN.setCellRenderer((l, v, i, s, f) -> {
            JPanel item = new JPanel(new BorderLayout());
            item.setOpaque(false); item.setBorder(new EmptyBorder(10,0,10,0));
            JLabel left = new JLabel("⚽ " + v.getMandante().getNome() + " x " + v.getVisitante().getNome());
            left.setForeground(UIUtils.TEXT);
            JLabel right = new JLabel(v.getDataHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")));
            right.setForeground(UIUtils.TEXT_DIM);
            item.add(left, BorderLayout.WEST);
            item.add(right, BorderLayout.EAST);
            return item;
        });
        try {
            pDAO.listarTodas().stream().filter(pa -> !pa.isFinalizada()).limit(6).forEach(mN::addElement);
        } catch (SQLException e) {}
        JScrollPane scrollNext = new JScrollPane(listN);
        scrollNext.setBorder(null);
        scrollNext.setOpaque(false);
        scrollNext.getViewport().setOpaque(false);
        nextCard.add(scrollNext, BorderLayout.CENTER);

        grid.add(statsCard); grid.add(nextCard);
        
        p.add(welcome, BorderLayout.NORTH);
        p.add(grid, BorderLayout.CENTER);
        return p;
    }

    private void atualizarStats(JTextArea txt) {
        if (!(user instanceof Participante)) {
            txt.setText("ADMINISTRADOR\nSistema Ativo");
            return;
        }
        try {
            List<Aposta> list = aDAO.listarPorUsuario(user.getId());
            int pts = 0; for (Aposta a : list) pts += calc.calcularPontos(a);
            txt.setText("🏆 " + pts + " Pontos\n🎯 " + list.size() + " Apostas");
        } catch (SQLException e) { txt.setText("Erro nos dados."); }
    }

    private JPanel panelPartidas() {
        JPanel p = createBasePanel();
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setOpaque(false); split.setBorder(null); split.setDividerLocation(450);

        DefaultListModel<Partida> model = new DefaultListModel<>();
        try { pDAO.listarTodas().forEach(model::addElement); } catch (SQLException e) {}
        
        JList<Partida> list = new JList<>(model);
        list.setBackground(UIUtils.BG);
        list.setForeground(UIUtils.TEXT);
        list.setOpaque(true);
        list.setSelectionBackground(UIUtils.SURFACE);
        list.setFixedCellHeight(60);
        list.setCellRenderer((l, v, i, s, f) -> {
            JPanel c = new JPanel(new BorderLayout(15, 0));
            c.setBackground(s ? UIUtils.SURFACE : UIUtils.BG);
            c.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER));
            JLabel info = new JLabel("  " + (v.isFinalizada() ? "✅ " : "⏳ ") + v.getMandante().getNome() + " x " + v.getVisitante().getNome());
            info.setFont(new Font("Inter", s ? Font.BOLD : Font.PLAIN, 14));
            info.setForeground(UIUtils.TEXT);
            c.add(info, BorderLayout.CENTER);
            return c;
        });

        JPanel details = UIUtils.createCard();
        JTextArea txt = new JTextArea("Selecione uma partida para ver detalhes.");
        txt.setEditable(false); txt.setOpaque(false);
        txt.setFont(new Font("Inter", Font.PLAIN, 15));
        txt.setForeground(UIUtils.TEXT);
        details.add(txt);

        list.addListSelectionListener(e -> {
            Partida sel = list.getSelectedValue();
            if (sel != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("DETALHES DA PARTIDA\n\n");
                sb.append("⚽ ").append(sel.getMandante().getNome()).append(" vs ").append(sel.getVisitante().getNome()).append("\n");
                sb.append("📅 ").append(sel.getDataHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
                sb.append("🏟️ Status: ").append(sel.isFinalizada() ? "FINALIZADA ("+sel.getGolsMandante()+"x"+sel.getGolsVisitante()+")" : "PENDENTE").append("\n\n");
                if (!sel.isFinalizada()) {
                    java.time.Duration d = java.time.Duration.between(LocalDateTime.now(), sel.getDataHoraInicio());
                    if (d.isNegative()) sb.append("⏱️ Jogo em andamento!");
                    else sb.append("⏳ Começa em: ").append(d.toHours()).append("h ").append(d.toMinutesPart()).append("m");
                }
                txt.setText(sb.toString());
            }
        });

        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UIUtils.BG);
        split.setLeftComponent(scroll);
        split.setRightComponent(details);
        p.add(split, BorderLayout.CENTER);
        return p;
    }

    private JPanel panelApostas() {
        JPanel p = createBasePanel();
        JPanel form = UIUtils.createCard();
        form.setPreferredSize(new Dimension(350, 0));
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.gridx = 0; g.weightx = 1.0;

        JComboBox<Partida> cb = new JComboBox<>(mP);
        cb.setBackground(UIUtils.SURFACE); cb.setForeground(UIUtils.TEXT);
        JTextField tM = UIUtils.createField(); JTextField tV = UIUtils.createField();
        JButton btn = UIUtils.createButton("Confirmar Palpite", true, false);

        g.gridy = 0; form.add(UIUtils.createLabel("ESCOLHA O JOGO", 11, true), g);
        g.gridy = 1; g.insets = new Insets(5,0,20,0); form.add(cb, g);
        g.gridy = 2; g.insets = new Insets(0,0,5,0); form.add(UIUtils.createLabel("GOLS MANDANTE", 11, true), g);
        g.gridy = 3; g.insets = new Insets(0,0,20,0); form.add(tM, g);
        g.gridy = 4; g.insets = new Insets(0,0,5,0); form.add(UIUtils.createLabel("GOLS VISITANTE", 11, true), g);
        g.gridy = 5; g.insets = new Insets(0,0,30,0); form.add(tV, g);
        g.gridy = 6; form.add(btn, g);

        btn.addActionListener(e -> {
            try {
                if (user instanceof Administrador) {
                    UIUtils.showError(this, "Administradores não podem realizar apostas.");
                    return;
                }
                Aposta a = new Aposta((Partida)cb.getSelectedItem(), Integer.parseInt(tM.getText()), Integer.parseInt(tV.getText()), LocalDateTime.now());
                aDAO.salvar(a, user.getId());
                UIUtils.showSuccess(this, "Aposta registrada!");
                loadData();
            } catch (NumberFormatException nfe) {
                UIUtils.showError(this, "Informe placares válidos (números).");
            } catch (Exception ex) { 
                UIUtils.showError(this, ex.getMessage()); 
            }
        });

        p.add(form, BorderLayout.WEST);
        p.add(UIUtils.createLabel("Suas Apostas", 18, true), BorderLayout.NORTH);
        return p;
    }

    private JPanel panelGrupos() {
        JPanel p = createBasePanel();
        JPanel top = new JPanel(new GridLayout(1, 2, 30, 0)); top.setOpaque(false);
        
        JPanel c1 = UIUtils.createCard();
        JTextField tN = UIUtils.createField(); JButton bC = UIUtils.createButton("Criar Grupo", true, true);
        c1.add(UIUtils.createLabel("NOVO GRUPO", 11, true), BorderLayout.NORTH);
        c1.add(tN, BorderLayout.CENTER); c1.add(bC, BorderLayout.SOUTH);

        JPanel c2 = UIUtils.createCard();
        JComboBox<GrupoAposta> cbG = new JComboBox<>(mG);
        cbG.setBackground(UIUtils.SURFACE); cbG.setForeground(UIUtils.TEXT);
        JButton bE = UIUtils.createButton("Entrar no Grupo", false, true);
        c2.add(UIUtils.createLabel("PARTICIPAR", 11, true), BorderLayout.NORTH);
        c2.add(cbG, BorderLayout.CENTER); c2.add(bE, BorderLayout.SOUTH);

        bC.addActionListener(e -> {
            try { 
                if (user instanceof Administrador) {
                    UIUtils.showError(this, "Administradores não podem criar grupos de apostas.");
                    return;
                }
                gDAO.salvar(new GrupoAposta(tN.getText(), (Participante)user)); 
                UIUtils.showSuccess(this, "Grupo criado!"); 
                loadData(); 
            }
            catch (Exception ex) { UIUtils.showError(this, "Erro ao criar grupo: " + ex.getMessage()); }
        });
        bE.addActionListener(e -> {
            try { 
                if (user instanceof Administrador) {
                    UIUtils.showError(this, "Administradores não podem participar de grupos.");
                    return;
                }
                gDAO.adicionarUsuarioAoGrupo(((GrupoAposta)cbG.getSelectedItem()).getId(), user.getId()); 
                UIUtils.showSuccess(this, "Você entrou!"); 
                loadData(); 
            }
            catch (Exception ex) { UIUtils.showError(this, "Erro ao entrar no grupo: " + ex.getMessage()); }
        });

        top.add(c1); top.add(c2);
        p.add(top, BorderLayout.NORTH);
        return p;
    }

    private JPanel panelRanking() {
        JPanel p = createBasePanel();
        p.setLayout(new BorderLayout(30, 0));

        // Título e Seletor de Grupo
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(UIUtils.createLabel("CLASSIFICAÇÕES", 24, true), BorderLayout.WEST);
        
        JPanel groupSelector = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        groupSelector.setOpaque(false);
        groupSelector.add(UIUtils.createLabel("FILTRAR POR GRUPO:", 11, true));
        JComboBox<GrupoAposta> cb = new JComboBox<>(mG);
        cb.setBackground(UIUtils.SURFACE); cb.setForeground(UIUtils.TEXT);
        cb.setPreferredSize(new Dimension(200, 35));
        groupSelector.add(cb);
        top.add(groupSelector, BorderLayout.EAST);
        
        p.add(top, BorderLayout.NORTH);

        // Painel Central com os dois Rankings
        JPanel grid = new JPanel(new GridLayout(1, 2, 30, 0));
        grid.setOpaque(false);

        // --- RANKING GLOBAL ---
        JPanel cardGlobal = UIUtils.createCard();
        cardGlobal.setLayout(new BorderLayout(0, 15));
        cardGlobal.add(UIUtils.createLabel("RANKING GLOBAL INDIVIDUAL", 12, true), BorderLayout.NORTH);
        
        DefaultListModel<String> mGlobal = new DefaultListModel<>();
        JList<String> listGlobal = new JList<>(mGlobal);
        listGlobal.setBackground(UIUtils.BG); listGlobal.setForeground(UIUtils.TEXT);
        listGlobal.setOpaque(true); listGlobal.setSelectionBackground(UIUtils.SURFACE);
        listGlobal.setFixedCellHeight(45);
        listGlobal.setFont(new Font("Inter", Font.PLAIN, 14));
        
        JScrollPane scrollGlobal = new JScrollPane(listGlobal);
        scrollGlobal.setBorder(null); scrollGlobal.getViewport().setBackground(UIUtils.BG);
        cardGlobal.add(scrollGlobal, BorderLayout.CENTER);

        // --- RANKING POR GRUPO ---
        JPanel cardGroup = UIUtils.createCard();
        cardGroup.setLayout(new BorderLayout(0, 15));
        cardGroup.add(UIUtils.createLabel("👥 RANKING DO GRUPO", 12, true), BorderLayout.NORTH);
        
        DefaultListModel<String> mGroup = new DefaultListModel<>();
        JList<String> listGroup = new JList<>(mGroup);
        listGroup.setBackground(UIUtils.BG); listGroup.setForeground(UIUtils.TEXT);
        listGroup.setOpaque(true); listGroup.setSelectionBackground(UIUtils.SURFACE);
        listGroup.setFixedCellHeight(45);
        listGroup.setFont(new Font("Inter", Font.PLAIN, 14));
        
        JScrollPane scrollGroup = new JScrollPane(listGroup);
        scrollGroup.setBorder(null); scrollGroup.getViewport().setBackground(UIUtils.BG);
        cardGroup.add(scrollGroup, BorderLayout.CENTER);

        grid.add(cardGlobal);
        grid.add(cardGroup);
        p.add(grid, BorderLayout.CENTER);

        // Lógica de Atualização Automática
        Runnable refreshGlobal = () -> {
            mGlobal.clear();
            try {
                List<Participante> all = new UsuarioDAO().listarParticipantes();
                Map<Participante, Integer> pts = new HashMap<>();
                for (Participante part : all) {
                    int t = 0;
                    for (Aposta a : aDAO.listarPorUsuario(part.getId())) t += calc.calcularPontos(a);
                    pts.put(part, t);
                }
                pts.entrySet().stream()
                   .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                   .forEach(en -> mGlobal.addElement(String.format("  %-20s %d PTS", en.getKey().getNome(), en.getValue())));
            } catch (Exception ex) { ex.printStackTrace(); }
        };

        Runnable refreshGroup = () -> {
            GrupoAposta g = (GrupoAposta) cb.getSelectedItem();
            mGroup.clear();
            if (g == null) return;
            try {
                Map<Participante, Integer> pts = new HashMap<>();
                for (Participante part : g.getParticipantes()) {
                    int t = 0;
                    for (Aposta a : aDAO.listarPorUsuario(part.getId())) t += calc.calcularPontos(a);
                    pts.put(part, t);
                }
                pts.entrySet().stream()
                   .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                   .forEach(en -> mGroup.addElement(String.format("  %-20s %d PTS", en.getKey().getNome(), en.getValue())));
            } catch (Exception ex) { ex.printStackTrace(); }
        };

        cb.addActionListener(e -> refreshGroup.run());
        
        // Timer para atualizar o Global periodicamente ou quando a aba abre
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                refreshGlobal.run();
                refreshGroup.run();
            }
        });

        return p;
    }

    private JPanel panelAdminTimes() {
        JPanel p = createBasePanel();
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setOpaque(false); split.setBorder(null); split.setDividerLocation(380);

        // FORMULÁRIO (ESQUERDA)
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        JPanel form = UIUtils.createCard();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.gridx = 0; g.weightx = 1.0;

        JTextField t = UIUtils.createField(); 
        JButton b = UIUtils.createButton("Cadastrar Nova Equipe", true, false);
        t.addActionListener(e -> b.doClick());

        g.gridy = 0; g.insets = new Insets(0,0,10,0);
        form.add(UIUtils.createLabel("GESTÃO DE TIMES", 18, true), g);
        g.gridy = 1; g.insets = new Insets(0,0,30,0);
        form.add(UIUtils.createLabel("Adicione novos clubes ao campeonato.", 13, false), g);
        
        g.gridy = 2; g.insets = new Insets(0,0,5,0);
        form.add(UIUtils.createLabel("NOME DA EQUIPE", 11, true), g);
        g.gridy = 3; g.insets = new Insets(0,0,25,0);
        form.add(t, g);
        g.gridy = 4;
        form.add(b, g);

        left.add(form, BorderLayout.NORTH);

        // LISTA (DIREITA)
        JPanel right = UIUtils.createCard();
        right.setLayout(new BorderLayout(0, 15));
        right.add(UIUtils.createLabel("EQUIPES CADASTRADAS", 12, true), BorderLayout.NORTH);

        JList<Time> listT = new JList<>(mT);
        listT.setBackground(UIUtils.BG); listT.setForeground(UIUtils.TEXT);
        listT.setOpaque(true); listT.setSelectionBackground(UIUtils.SURFACE);
        listT.setFixedCellHeight(50);
        listT.setCellRenderer((l, v, i, s, f) -> {
            JLabel lbl = new JLabel("   🛡️  " + v.getNome());
            lbl.setFont(new Font("Inter", s ? Font.BOLD : Font.PLAIN, 14));
            lbl.setForeground(UIUtils.TEXT);
            lbl.setOpaque(true);
            lbl.setBackground(s ? UIUtils.SURFACE : UIUtils.BG);
            lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER));
            return lbl;
        });

        JScrollPane scrollT = new JScrollPane(listT);
        scrollT.setBorder(null); scrollT.getViewport().setBackground(UIUtils.BG);
        right.add(scrollT, BorderLayout.CENTER);

        b.addActionListener(e -> {
            try { 
                if (t.getText().trim().isEmpty()) throw new Exception("Nome vazio");
                tDAO.salvar(new Time(t.getText().trim())); 
                loadData(); t.setText(""); 
                UIUtils.showSuccess(this, "Equipe cadastrada!"); 
            }
            catch (Exception ex) { UIUtils.showError(this, "Erro: " + ex.getMessage()); }
        });

        split.setLeftComponent(left);
        split.setRightComponent(right);
        p.add(split, BorderLayout.CENTER);
        return p;
    }

    private JPanel panelAdminPartidas() {
        JPanel p = createBasePanel();
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setOpaque(false); split.setBorder(null); split.setDividerLocation(380);

        // FORMULÁRIO (ESQUERDA)
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        JPanel form = UIUtils.createCard();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.gridx = 0; g.weightx = 1.0;

        JComboBox<Time> cbM = new JComboBox<>(mT); 
        cbM.setBackground(UIUtils.SURFACE); cbM.setForeground(UIUtils.TEXT);
        JComboBox<Time> cbV = new JComboBox<>(mT_V);
        cbV.setBackground(UIUtils.SURFACE); cbV.setForeground(UIUtils.TEXT);
        
        JSpinner spinData = new JSpinner(new SpinnerDateModel());
        spinData.setEditor(new JSpinner.DateEditor(spinData, "dd/MM/yyyy"));
        
        JSpinner spinHora = new JSpinner(new SpinnerDateModel());
        spinHora.setEditor(new JSpinner.DateEditor(spinHora, "HH:mm"));

        JPanel pnlDataHora = new JPanel(new GridLayout(1, 2, 10, 0));
        pnlDataHora.setOpaque(false);
        pnlDataHora.add(spinData);
        pnlDataHora.add(spinHora);

        JButton btn = UIUtils.createButton("Agendar Partida", true, false);

        g.gridy = 0; g.insets = new Insets(0,0,10,0);
        form.add(UIUtils.createLabel("AGENDAR JOGOS", 18, true), g);
        g.gridy = 1; g.insets = new Insets(0,0,30,0);
        form.add(UIUtils.createLabel("Crie novos confrontos no sistema.", 13, false), g);

        g.gridy = 2; g.insets = new Insets(0,0,5,0);
        form.add(UIUtils.createLabel("MANDANTE", 10, true), g);
        g.gridy = 3; g.insets = new Insets(0,0,15,0);
        form.add(cbM, g);

        g.gridy = 4; g.insets = new Insets(0,0,5,0);
        form.add(UIUtils.createLabel("VISITANTE", 10, true), g);
        g.gridy = 5; g.insets = new Insets(0,0,15,0);
        form.add(cbV, g);

        g.gridy = 6; g.insets = new Insets(0,0,5,0);
        form.add(UIUtils.createLabel("DATA E HORÁRIO", 10, true), g);
        g.gridy = 7; g.insets = new Insets(0,0,30,0);
        form.add(pnlDataHora, g);

        g.gridy = 8;
        form.add(btn, g);

        left.add(form, BorderLayout.NORTH);

        // LISTA (DIREITA)
        JPanel right = UIUtils.createCard();
        right.setLayout(new BorderLayout(0, 15));
        right.add(UIUtils.createLabel("PARTIDAS AGENDADAS", 12, true), BorderLayout.NORTH);

        JList<Partida> listP = new JList<>(mP);
        listP.setBackground(UIUtils.BG); listP.setForeground(UIUtils.TEXT);
        listP.setOpaque(true); listP.setSelectionBackground(UIUtils.SURFACE);
        listP.setFixedCellHeight(60);
        listP.setCellRenderer((l, v, i, s, f) -> {
            JPanel c = new JPanel(new BorderLayout(15, 0));
            c.setBackground(s ? UIUtils.SURFACE : UIUtils.BG);
            c.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER));
            
            JLabel info = new JLabel("  ⚡ " + v.getMandante().getNome() + " x " + v.getVisitante().getNome());
            info.setFont(new Font("Inter", s ? Font.BOLD : Font.PLAIN, 14));
            info.setForeground(UIUtils.TEXT);
            
            JLabel date = new JLabel(v.getDataHoraInicio().format(DateTimeFormatter.ofPattern("dd/MM HH:mm")) + "  ");
            date.setForeground(UIUtils.TEXT_DIM);
            date.setFont(new Font("Inter", Font.PLAIN, 12));

            c.add(info, BorderLayout.CENTER);
            c.add(date, BorderLayout.EAST);
            return c;
        });

        JScrollPane scrollP = new JScrollPane(listP);
        scrollP.setBorder(null); scrollP.getViewport().setBackground(UIUtils.BG);
        right.add(scrollP, BorderLayout.CENTER);

        btn.addActionListener(e -> {
            try {
                if (cbM.getSelectedItem() == cbV.getSelectedItem()) throw new Exception("Times iguais");
                
                java.util.Date dData = (java.util.Date)spinData.getValue();
                java.time.LocalDate localDate = dData.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                
                java.util.Date dHora = (java.util.Date)spinHora.getValue();
                java.time.LocalTime localTime = dHora.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime();
                
                LocalDateTime dt = LocalDateTime.of(localDate, localTime);
                
                pDAO.salvar(new Partida((Time)cbM.getSelectedItem(), (Time)cbV.getSelectedItem(), dt));
                loadData(); UIUtils.showSuccess(this, "Partida agendada!");
            } catch (Exception ex) { UIUtils.showError(this, "Erro: " + ex.getMessage()); }
        });

        split.setLeftComponent(left);
        split.setRightComponent(right);
        p.add(split, BorderLayout.CENTER);
        return p;
    }

    private JPanel panelAdminResultados() {
        JPanel p = createBasePanel();
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        split.setOpaque(false); split.setBorder(null); split.setDividerLocation(380);

        // FORMULÁRIO (ESQUERDA)
        JPanel left = new JPanel(new BorderLayout());
        left.setOpaque(false);
        JPanel form = UIUtils.createCard();
        form.setLayout(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL; g.gridx = 0; g.weightx = 1.0;

        JComboBox<Partida> cb = new JComboBox<>(mP);
        cb.setBackground(UIUtils.SURFACE); cb.setForeground(UIUtils.TEXT);
        JTextField tM = UIUtils.createField(); JTextField tV = UIUtils.createField();
        JButton bS = UIUtils.createButton("Registrar Placar", true, false);
        JButton bRandom = UIUtils.createButton("🎲 Aleatorizar Todos", false, false);

        g.gridy = 0; g.insets = new Insets(0,0,10,0);
        form.add(UIUtils.createLabel("RESULTADOS", 18, true), g);
        g.gridy = 1; g.insets = new Insets(0,0,30,0);
        form.add(UIUtils.createLabel("Lançar o placar final das partidas.", 13, false), g);

        g.gridy = 2; g.insets = new Insets(0,0,5,0);
        form.add(UIUtils.createLabel("SELECIONE A PARTIDA", 10, true), g);
        g.gridy = 3; g.insets = new Insets(0,0,20,0);
        form.add(cb, g);

        g.gridy = 4; g.insets = new Insets(0,0,5,0);
        form.add(UIUtils.createLabel("GOLS MANDANTE", 10, true), g);
        g.gridy = 5; g.insets = new Insets(0,0,15,0);
        form.add(tM, g);

        g.gridy = 6; g.insets = new Insets(0,0,5,0);
        form.add(UIUtils.createLabel("GOLS VISITANTE", 10, true), g);
        g.gridy = 7; g.insets = new Insets(0,0,30,0);
        form.add(tV, g);

        g.gridy = 8; g.insets = new Insets(0,0,10,0);
        form.add(bS, g);
        g.gridy = 9; g.insets = new Insets(0,0,0,0);
        form.add(bRandom, g);

        left.add(form, BorderLayout.NORTH);

        // LISTA (DIREITA)
        JPanel right = UIUtils.createCard();
        right.setLayout(new BorderLayout(0, 15));
        right.add(UIUtils.createLabel("HISTÓRICO DE PARTIDAS", 12, true), BorderLayout.NORTH);

        JList<Partida> listP = new JList<>(mP);
        listP.setBackground(UIUtils.BG); listP.setForeground(UIUtils.TEXT);
        listP.setOpaque(true); listP.setSelectionBackground(UIUtils.SURFACE);
        listP.setFixedCellHeight(60);
        listP.setCellRenderer((l, v, i, s, f) -> {
            JPanel c = new JPanel(new BorderLayout(15, 0));
            c.setBackground(s ? UIUtils.SURFACE : UIUtils.BG);
            c.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER));
            
            String status = v.isFinalizada() ? String.format("   %d x %d ", v.getGolsMandante(), v.getGolsVisitante()) : "   vs   ";
            JLabel info = new JLabel("   " + v.getMandante().getNome() + status + v.getVisitante().getNome());
            info.setFont(new Font("Inter", s ? Font.BOLD : Font.PLAIN, 14));
            info.setForeground(UIUtils.TEXT);
            
            JLabel badge = new JLabel(v.isFinalizada() ? "ENCERRADO " : "PENDENTE ");
            badge.setForeground(v.isFinalizada() ? UIUtils.COLOR_SUCCESS : UIUtils.TEXT_DIM);
            badge.setFont(new Font("Inter", Font.BOLD, 10));

            c.add(info, BorderLayout.CENTER);
            c.add(badge, BorderLayout.EAST);
            return c;
        });

        JScrollPane scrollP = new JScrollPane(listP);
        scrollP.setBorder(null); scrollP.getViewport().setBackground(UIUtils.BG);
        right.add(scrollP, BorderLayout.CENTER);

        bS.addActionListener(e -> {
            try {
                Partida part = (Partida) cb.getSelectedItem();
                if (part == null) throw new Exception("Selecione uma partida");
                part.finalizarPartida(Integer.parseInt(tM.getText()), Integer.parseInt(tV.getText()));
                pDAO.atualizar(part); loadData(); 
                UIUtils.showSuccess(this, "Placar atualizado!");
            } catch (Exception ex) { UIUtils.showError(this, "Erro: " + ex.getMessage()); }
        });

        bRandom.addActionListener(e -> {
            try {
                java.util.Random rand = new java.util.Random();
                int count = 0;
                for (int i = 0; i < mP.getSize(); i++) {
                    Partida p_atual = mP.getElementAt(i);
                    if (!p_atual.isFinalizada()) {
                        p_atual.finalizarPartida(rand.nextInt(6), rand.nextInt(6)); // Gols de 0 a 5
                        pDAO.atualizar(p_atual);
                        count++;
                    }
                }
                loadData();
                UIUtils.showSuccess(this, count + " partidas finalizadas!");
            } catch (Exception ex) {
                UIUtils.showError(this, "Erro ao aleatorizar");
            }
        });

        split.setLeftComponent(left);
        split.setRightComponent(right);
        p.add(split, BorderLayout.CENTER);
        return p;
    }

    private JPanel createBasePanel() {
        JPanel p = new JPanel(new BorderLayout(30, 30));
        p.setOpaque(false); p.setBorder(new EmptyBorder(40, 50, 40, 50));
        return p;
    }
}
