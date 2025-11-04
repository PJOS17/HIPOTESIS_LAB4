
import java.awt.*;
import java.util.Map;
import javax.swing.*;

public class VistaAnalisis extends JFrame {
    private final ControladorAnalisis ctrl;

    public VistaAnalisis(ControladorAnalisis ctrl) {
        super("Análisis");
        if (ctrl == null) {
            throw new IllegalArgumentException("Controlador no puede ser null");
        }
        this.ctrl = ctrl;
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        init();
    }

    private void init() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        Map<String, Integer> freq = ctrl.frecuenciaPalabras();
        StringBuilder sb = new StringBuilder();
        sb.append("Análisis de frecuencia de palabras\n");
        sb.append("=================================\n\n");
        
        freq.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(100)
            .forEach(e -> sb.append(String.format("%-20s: %d%n", e.getKey(), e.getValue())));
        
        area.setText(sb.toString());
        
        JScrollPane scrollPane = new JScrollPane(area);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Agregar botón para cerrar
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(btnCerrar);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
