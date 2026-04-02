import Sistema.SistemaGUI;

import javax.swing.*;

public static void main(String[] args) {
    try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    } catch (Exception e) {
        e.printStackTrace();
    }
    SwingUtilities.invokeLater(() -> new SistemaGUI().setVisible(true));
}
