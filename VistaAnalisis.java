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

        sb.append("\n\nResumen final:\n");
        sb.append("---------------------------\n");
        sb.append(String.format("Palabras totales: %d%n", ctrl.contarPalabrasTotal()));
        sb.append(String.format("Etiquetas: %d%n", ctrl.contarEtiquetas()));
        sb.append(String.format("Correcciones marcadas: %d%n", ctrl.contarCorrecciones()));
        sb.append("\nHipótesis:\n");
        if (ctrl.getHipotesis() == null || ctrl.getHipotesis().isEmpty()) {
            sb.append(" (ninguna)\n");
        } else {
            for (String h : ctrl.getHipotesis()) {
                sb.append(" - ").append(h).append("\n");
            }
        }

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
