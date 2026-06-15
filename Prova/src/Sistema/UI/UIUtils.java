package Sistema.UI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class UIUtils {
    // Paleta Soft Dark (Moderna e com Alto Contraste)
    public static final Color ACCENT = new Color(59, 130, 246);  // Azul Brilhante
    public static final Color COLOR_SUCCESS = new Color(34, 197, 94);  // Verde Vibrante
    public static final Color COLOR_ERROR = new Color(239, 68, 68);    // Vermelho Vibrante
    public static final Color BG = new Color(15, 23, 42);       // Fundo Principal (Slate 900)
    public static final Color SURFACE = new Color(30, 41, 59);  // Superfícies/Cards (Slate 800)
    public static final Color BORDER = new Color(51, 65, 85);   // Bordas (Slate 700)
    public static final Color TEXT = new Color(248, 250, 252);   // Texto Principal (Slate 50)
    public static final Color TEXT_DIM = new Color(148, 163, 184); // Texto Secundário (Slate 400)

    public static JButton createButton(String text, boolean primary, boolean small) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (primary) {
                    g2.setColor(getModel().isPressed() ? ACCENT.darker() : (getModel().isRollover() ? ACCENT.brighter() : ACCENT));
                } else {
                    g2.setColor(getModel().isPressed() ? BG : (getModel().isRollover() ? BORDER : SURFACE));
                }
                
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                
                // Borda para todos para maior definição
                g2.setColor(primary ? ACCENT.darker() : BORDER);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 10, 10));
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Inter", Font.BOLD, small ? 12 : 14));
        btn.setForeground(primary ? Color.WHITE : TEXT);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        int h = small ? 34 : 46;
        btn.setPreferredSize(new Dimension(160, h));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, h));
        return btn;
    }

    public static JTextField createField() {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fundo do campo
                g2.setColor(BG); 
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                
                // Borda completa sempre visível
                g2.setColor(isFocusOwner() ? ACCENT : BORDER);
                g2.setStroke(new BasicStroke(isFocusOwner() ? 1.5f : 1.0f));
                g2.draw(new RoundRectangle2D.Double(0.5, 0.5, getWidth() - 1, getHeight() - 1, 10, 10));
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        f.setFont(new Font("Inter", Font.PLAIN, 15));
        f.setBackground(new Color(0,0,0,0));
        f.setForeground(TEXT);
        f.setCaretColor(ACCENT);
        f.setOpaque(false);
        f.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        f.setPreferredSize(new Dimension(200, 42));
        return f;
    }

    public static JPanel createCard() {
        JPanel p = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(SURFACE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 16, 16));
                
                g2.setColor(BORDER);
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 16, 16));
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(25, 25, 25, 25));
        return p;
    }

    public static JLabel createLabel(String text, int size, boolean bold) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Inter", bold ? Font.BOLD : Font.PLAIN, size));
        l.setForeground(bold ? TEXT : TEXT_DIM);
        return l;
    }

    public static void showSuccess(Component parent, String message) {
        Window window = (parent instanceof Window) ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(window);
        dialog.setUndecorated(true);
        dialog.setAlwaysOnTop(true);
        dialog.setLayout(new BorderLayout());
        dialog.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_SUCCESS);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        JLabel lbl = new JLabel("✓ " + message);
        lbl.setFont(new Font("Inter", Font.BOLD, 14));
        lbl.setForeground(Color.WHITE);
        panel.add(lbl);
        
        dialog.add(panel);
        dialog.pack();

        if (window != null && window.isVisible()) {
            Point p = window.getLocationOnScreen();
            dialog.setLocation(p.x + (window.getWidth() - dialog.getWidth()) / 2, p.y + 60);
        } else {
            dialog.setLocationRelativeTo(null);
        }

        dialog.setVisible(true);
        
        Timer t = new Timer(2500, e -> {
            dialog.dispose();
        });
        t.setRepeats(false);
        t.start();
    }

    public static void showError(Component parent, String message) {
        Window window = (parent instanceof Window) ? (Window) parent : SwingUtilities.getWindowAncestor(parent);
        JDialog dialog = new JDialog(window);
        dialog.setUndecorated(true);
        dialog.setAlwaysOnTop(true);
        dialog.setLayout(new BorderLayout());
        dialog.setBackground(new Color(0, 0, 0, 0));

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_ERROR);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        JLabel lbl = new JLabel("Erro: " + message);
        lbl.setFont(new Font("Inter", Font.BOLD, 14));
        lbl.setForeground(Color.WHITE);
        panel.add(lbl);
        
        dialog.add(panel);
        dialog.pack();

        if (window != null && window.isVisible()) {
            Point p = window.getLocationOnScreen();
            dialog.setLocation(p.x + (window.getWidth() - dialog.getWidth()) / 2, p.y + 60);
        } else {
            dialog.setLocationRelativeTo(null);
        }

        dialog.setVisible(true);
        
        Timer t = new Timer(3500, e -> {
            dialog.dispose();
        });
        t.setRepeats(false);
        t.start();
    }
}
