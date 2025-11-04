

import javax.swing.*;

public class Main {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            try {
                // look and feel del sistema
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored){}
            VistaPrincipal vp = new VistaPrincipal();
            ControladorPrincipal cp = new ControladorPrincipal(vp);
            vp.setCtrl(cp);
            vp.setVisible(true);
        });
    }
}
