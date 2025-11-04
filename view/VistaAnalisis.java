package view;

import controller.ControladorAnalisis;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class VistaAnalisis extends JFrame {
    private ControladorAnalisis ctrl;

    public VistaAnalisis(ControladorAnalisis ctrl){
        super("Analisis");
        this.ctrl = ctrl;
        setSize(600,500);
        setLocationRelativeTo(null);
        init();
    }

    private void init(){
        JTextArea area = new JTextArea();
        area.setEditable(false);
        var freq = ctrl.frecuenciaPalabras();
        StringBuilder sb = new StringBuilder();
        freq.entrySet().stream().sorted((a,b)->b.getValue()-a.getValue()).limit(100)
          .forEach(e -> sb.append(e.getKey()).append(": ").append(e.getValue()).append("\n"));
        area.setText(sb.toString());
        add(new JScrollPane(area), BorderLayout.CENTER);
    }
}

