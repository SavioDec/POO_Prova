package Sistema.UI;

import Sistema.Administrador;
import Sistema.DAO.UsuarioDAO;
import Sistema.Participante;
import Sistema.Pessoa;
import Sistema.SistemaGUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginGUI extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JComboBox<String> cbRole;
    private UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginGUI() {
        setTitle("FutBet Pro - Login");
        setSize(450, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(UIUtils.BG);

        JPanel main = new JPanel(new GridBagLayout());
        main.setOpaque(false);
        main.setBorder(new EmptyBorder(40, 40, 40, 40));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.weightx = 1.0;

        // Logo
        JLabel logo = UIUtils.createLabel("FUTBET PRO", 28, true);
        logo.setForeground(UIUtils.ACCENT);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 0; g.insets = new Insets(0, 0, 40, 0);
        main.add(logo, g);

        // Formulário
        g.insets = new Insets(0, 0, 5, 0);
        g.gridy = 1; main.add(UIUtils.createLabel("USUÁRIO", 11, true), g);
        g.gridy = 2; g.insets = new Insets(0, 0, 20, 0);
        txtUsuario = UIUtils.createField();
        txtUsuario.addActionListener(e -> logar()); // Enter to login
        main.add(txtUsuario, g);

        g.insets = new Insets(0, 0, 5, 0);
        g.gridy = 3; main.add(UIUtils.createLabel("SENHA", 11, true), g);
        g.gridy = 4; g.insets = new Insets(0, 0, 25, 0);
        txtSenha = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(UIUtils.BG);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.setColor(isFocusOwner() ? UIUtils.ACCENT : UIUtils.BORDER);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 1.5f : 1.0f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txtSenha.setOpaque(false);
        txtSenha.setBackground(new Color(0,0,0,0));
        txtSenha.setForeground(UIUtils.TEXT);
        txtSenha.setCaretColor(UIUtils.ACCENT);
        txtSenha.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        txtSenha.setPreferredSize(new Dimension(200, 42));
        txtSenha.addActionListener(e -> logar()); // Enter to login
        main.add(txtSenha, g);

        g.insets = new Insets(0, 0, 5, 0);
        g.gridy = 5; main.add(UIUtils.createLabel("TIPO DE CONTA", 11, true), g);
        g.gridy = 6; g.insets = new Insets(0, 0, 40, 0);
        cbRole = new JComboBox<>(new String[]{"Participante", "Administrador"});
        cbRole.setBackground(UIUtils.SURFACE);
        cbRole.setForeground(UIUtils.TEXT);
        cbRole.setFont(new Font("Inter", Font.PLAIN, 14));
        main.add(cbRole, g);

        // Botões
        JButton btnL = UIUtils.createButton("Entrar no Sistema", true, false);
        btnL.addActionListener(e -> logar());
        g.gridy = 7; g.insets = new Insets(0, 0, 15, 0);
        main.add(btnL, g);

        JButton btnC = UIUtils.createButton("Criar Nova Conta", false, false);
        btnC.addActionListener(e -> cadastrar());
        g.gridy = 8; g.insets = new Insets(0, 0, 0, 0);
        main.add(btnC, g);

        add(main);
    }

    private void stylePasswordField(JPasswordField f) {
        f.setFont(new Font("Inter", Font.PLAIN, 15));
        f.setBackground(UIUtils.BG);
        f.setForeground(UIUtils.TEXT);
        f.setCaretColor(UIUtils.ACCENT);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, UIUtils.BORDER),
            BorderFactory.createEmptyBorder(8, 0, 8, 0)
        ));
        f.setPreferredSize(new Dimension(200, 40));
    }

    private void logar() {
        try {
            Pessoa u = usuarioDAO.login(txtUsuario.getText(), new String(txtSenha.getPassword()));
            if (u != null) { new SistemaGUI(u).setVisible(true); dispose(); }
            else UIUtils.showError(this, "Credenciais inválidas"); 
        } catch (Exception e) { UIUtils.showError(this, "Erro no acesso"); }
    }

    private void cadastrar() {
        try {
            String n = txtUsuario.getText(); String s = new String(txtSenha.getPassword());
            if (n.isEmpty() || s.isEmpty()) {
                UIUtils.showError(this, "Preencha todos os campos");
                return;
            }
            Pessoa novo = cbRole.getSelectedIndex() == 0 ? new Participante(n) : new Administrador(n);
            novo.setSenha(s);
            usuarioDAO.salvar(novo);
            UIUtils.showSuccess(this, "Conta criada com sucesso!");
        } catch (Exception e) { UIUtils.showError(this, "Usuário já existe"); }
    }
}
