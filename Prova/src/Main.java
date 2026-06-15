import Sistema.Database.DatabaseConnection;
import Sistema.UI.LoginGUI;

import javax.swing.*;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {
        DatabaseConnection.inicializarBanco();

        // Limpeza completa do LookAndFeel para controle total Graphics2D
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); // Mais consistente para customização
            
            Color bg = new java.awt.Color(15, 23, 42);
            Color surface = new java.awt.Color(30, 41, 59);
            Color text = new java.awt.Color(248, 250, 252);
            Color border = new java.awt.Color(51, 65, 85);

            UIManager.put("Panel.background", bg);
            UIManager.put("Label.foreground", text);
            UIManager.put("List.background", bg);
            UIManager.put("List.foreground", text);
            UIManager.put("List.selectionBackground", surface);
            UIManager.put("List.selectionForeground", text);
            UIManager.put("ScrollPane.background", bg);
            UIManager.put("ScrollPane.border", BorderFactory.createEmptyBorder());
            UIManager.put("Viewport.background", bg);
            UIManager.put("Viewport.foreground", text);
            UIManager.put("TextArea.background", surface);
            UIManager.put("TextArea.foreground", text);
            UIManager.put("TextField.background", bg);
            UIManager.put("TextField.foreground", text);
            UIManager.put("ComboBox.background", surface);
            UIManager.put("ComboBox.foreground", text);
            UIManager.put("ComboBox.selectionBackground", surface);
            UIManager.put("ComboBox.selectionForeground", text);
            UIManager.put("Button.background", surface);
            UIManager.put("Button.foreground", text);
            UIManager.put("ScrollBar.background", bg);
            UIManager.put("ScrollBar.thumb", surface);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            LoginGUI login = new LoginGUI();
            login.setVisible(true);
        });
    }
}
