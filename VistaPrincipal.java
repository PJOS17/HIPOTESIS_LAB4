import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;

public class VistaPrincipal extends JFrame {
    private ControladorPrincipal ctrl;
    private ControladorAnalisis ctrlAnalisis;
    private JList<String> listaDocumentos;
    private DefaultListModel<String> modeloDocumentos;
    private DefaultListModel<String> modeloEtiquetas;
    private JList<String> listEtiquetas;
    private JTextPane areaTexto;
    private JTextField txtProyecto;
    private JTextArea txtNuevoDocumento;
    private JLabel lblHipotesis;
    private JLabel lblPalabras;
    private JLabel lblEtiquetas;
    private String hipotesisActual = "";
    private String documentoActualTitulo = "";
    private Map<String, String> estilosDocumentos = new java.util.HashMap<>();

    public VistaPrincipal() {
        super("Anthro-Analyzer Pro");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        initComponents();
    }

    public void setControlador(ControladorPrincipal c) {
        this.ctrl = c;
        this.ctrlAnalisis = new ControladorAnalisis(c.getProyecto());
        refreshAll();
    }

    public void refreshAll() {
        actualizarListaDocumentos();
        actualizarCodebook();
        if (ctrl != null && ctrl.getProyecto() != null) {
            txtProyecto.setText(ctrl.getProyecto().getNombre());
            // ya no mostramos la hipótesis en la UI principal
            List<String> hipotesis = ctrl.getProyecto().getHipotesis();
            hipotesisActual = (hipotesis != null && !hipotesis.isEmpty()) ? hipotesis.get(0) : "";
        }
        areaTexto.setText("");
        documentoActualTitulo = "";
        actualizarContadoresDocumento();
    }

    private void initComponents() {
        Font fuenteGrande = new Font("Arial", Font.PLAIN, 18);
        areaTexto = new JTextPane();
        areaTexto.setFont(fuenteGrande);

        // PANEL IZQUIERDO
        JPanel left = new JPanel(new BorderLayout());
        left.setPreferredSize(new Dimension(260, 600));

        txtProyecto = new JTextField("Proyecto nuevo", 12);

        // BOTÓN CAMBIADO: Guardar nombre -> Limpiar (limpia documento abierto)
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> {
            if (documentoActualTitulo.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Abra un documento para limpiar.");
                return;
            }
            int opt = JOptionPane.showConfirmDialog(this, "Limpiar el contenido del documento actual?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                areaTexto.setText("");
                guardarDocumentoActual(); // guarda el cambio en el objeto Documento
                // limpiar estilos guardados
                estilosDocumentos.remove(documentoActualTitulo);
                restaurarEstilosDocumento(documentoActualTitulo);
                actualizarContadoresDocumento();
            }
        });

        JButton btnNuevoDocumento = new JButton("Nuevo documento");
        btnNuevoDocumento.addActionListener(e -> crearNuevoDocumentoDialog());

        JPanel topLeft = new JPanel(new GridLayout(3, 1, 4, 4));
        topLeft.add(new JLabel("Proyecto:"));
        topLeft.add(txtProyecto);
        JPanel btnProyectoPanel = new JPanel(new GridLayout(1, 2, 4, 4));
        btnProyectoPanel.add(btnLimpiar);
        btnProyectoPanel.add(btnNuevoDocumento);
        topLeft.add(btnProyectoPanel);

        modeloDocumentos = new DefaultListModel<>();
        listaDocumentos = new JList<>(modeloDocumentos);
        JScrollPane scrollDocs = new JScrollPane(listaDocumentos);

        JButton btnCargarTexto = new JButton("Cargar archivo");
        btnCargarTexto.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                ctrl.agregarDocumentoDesdeArchivo(f);
                actualizarListaDocumentos();
            }
        });

        JButton btnAbrir = new JButton("Abrir documento");
        btnAbrir.addActionListener(e -> abrirDocumentoSeleccionado());

        JButton btnEliminarDoc = new JButton("Eliminar documento");
        btnEliminarDoc.addActionListener(e -> eliminarDocumento());

        JPanel leftButtons = new JPanel(new GridLayout(3, 1, 4, 4));
        leftButtons.add(btnCargarTexto);
        leftButtons.add(btnAbrir);
        leftButtons.add(btnEliminarDoc);

        left.add(topLeft, BorderLayout.NORTH);
        left.add(scrollDocs, BorderLayout.CENTER);
        left.add(leftButtons, BorderLayout.SOUTH);

        // PANEL CENTRAL
        JPanel center = new JPanel(new BorderLayout());
        JScrollPane scrollCenter = new JScrollPane(areaTexto);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        acciones.setPreferredSize(new Dimension(1200, 80));

        JButton btnCrearEtiqueta = new JButton("Crear etiqueta");
        btnCrearEtiqueta.addActionListener(e -> crearEtiquetaDialog());

        JButton btnAplicarEtiqueta = new JButton("Aplicar etiqueta");
        btnAplicarEtiqueta.addActionListener(e -> aplicarEtiquetaDialog());

        JButton btnResumen = new JButton("Resumen etiquetas");
        btnResumen.addActionListener(e -> resumenEtiquetaDialog());

        JButton btnHipotesis = new JButton("Hipótesis");
        btnHipotesis.addActionListener(e -> crearHipotesisDialog());

        JButton btnAnalisis = new JButton("Análisis de Resultados");
        btnAnalisis.addActionListener(e -> mostrarAnalisisCompleto());

        // BOTÓN: descargar análisis debajo del archivo editado
        JButton btnDescargarAnalisis = new JButton("Descargar Análisis");
        btnDescargarAnalisis.addActionListener(e -> descargarAnalisisDebajo());

        JButton btnGuardarProyecto = new JButton("Guardar Proyecto");
        btnGuardarProyecto.addActionListener(e -> guardarProyectoDialog());

        acciones.add(btnCrearEtiqueta);
        acciones.add(btnAplicarEtiqueta);
        acciones.add(btnResumen);
        acciones.add(btnHipotesis);
        acciones.add(btnAnalisis);
        acciones.add(btnDescargarAnalisis);
        acciones.add(btnGuardarProyecto);

        center.add(acciones, BorderLayout.NORTH);
        center.add(scrollCenter, BorderLayout.CENTER);

        // PANEL DERECHO - reemplazado para mostrar etiquetas con color y habilitar botones
        JPanel right = new JPanel(new BorderLayout());
        modeloEtiquetas = new DefaultListModel<>();
        listEtiquetas = new JList<>(modeloEtiquetas);
        listEtiquetas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollEtiq = new JScrollPane(listEtiquetas);

        // botones de etiqueta como variables locales visibles en este bloque
        JButton btnVerEtiqueta = new JButton("Ver etiqueta");
        JButton btnEditarEtiqueta = new JButton("Editar etiqueta");
        btnVerEtiqueta.setEnabled(false);
        btnEditarEtiqueta.setEnabled(false);

        // Renderer que muestra un cuadrado de color junto al nombre
        listEtiquetas.setCellRenderer(new DefaultListCellRenderer() {
            private final Icon emptyIcon = new ColorIcon(null);
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                String nombre = value == null ? "" : value.toString();
                Etiqueta et = (ctrl != null && ctrl.getProyecto() != null) ? ctrl.getProyecto().buscarEtiquetaPorNombre(nombre) : null;
                if (et != null && et.getColor() != null) {
                    setIcon(new ColorIcon(parseColorString(et.getColor())));
                } else {
                    setIcon(emptyIcon);
                }
                setText(nombre);
                return this;
            }
        });

        // List selection listener para habilitar botones
        listEtiquetas.addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting()) {
                boolean sel = listEtiquetas.getSelectedIndex() >= 0;
                btnVerEtiqueta.setEnabled(sel);
                btnEditarEtiqueta.setEnabled(sel);
            }
        });

        // doble click para ver etiqueta
        listEtiquetas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    mostrarAnalisisEtiquetaSeleccionada();
                }
            }
        });

        // acciones de botones
        btnVerEtiqueta.addActionListener(e -> mostrarAnalisisEtiquetaSeleccionada());
        btnEditarEtiqueta.addActionListener(e -> editarEtiquetaDialog());

        JPanel etiqButtons = new JPanel(new GridLayout(1, 2, 4, 4));
        etiqButtons.add(btnVerEtiqueta);
        etiqButtons.add(btnEditarEtiqueta);

        // meta panel (contadores)
        lblPalabras = new JLabel("Palabras: 0");
        lblPalabras.setFont(new Font("Arial", Font.PLAIN, 12));
        lblEtiquetas = new JLabel("Etiquetas en doc: 0");
        lblEtiquetas.setFont(new Font("Arial", Font.PLAIN, 12));

        JPanel metaPanel = new JPanel(new GridLayout(2,1));
        metaPanel.add(lblPalabras);
        metaPanel.add(lblEtiquetas);

        right.add(new JLabel("Codebook"), BorderLayout.NORTH);
        right.add(scrollEtiq, BorderLayout.CENTER);
        right.add(etiqButtons, BorderLayout.SOUTH);
        right.add(metaPanel, BorderLayout.PAGE_END);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(left, BorderLayout.WEST);
        getContentPane().add(center, BorderLayout.CENTER);
        getContentPane().add(right, BorderLayout.EAST);
    }

    private void abrirDocumentoSeleccionado() {
        // Guardar documento anterior antes de cambiar
        if (!documentoActualTitulo.isEmpty()) {
            guardarDocumentoActual();
        }

        int idx = listaDocumentos.getSelectedIndex();
        if (idx >= 0) {
            Documento d = ctrl.getProyecto().getDocumentos().get(idx);
            documentoActualTitulo = d.getTitulo();
            areaTexto.setText(d.getContenido());
            
            // Restaurar estilos del documento
            restaurarEstilosDocumento(d.getTitulo());
            
            // Actualizar análisis según el documento
            actualizarAnalisisDocumento();
            actualizarContadoresDocumento();
        }
    }

    private void guardarDocumentoActual() {
        if (documentoActualTitulo.isEmpty()) return;
        
        // Guardar contenido
        Documento doc = ctrl.getProyecto().buscarDocumentoPorTitulo(documentoActualTitulo);
        if (doc != null) {
            doc.setContenido(areaTexto.getText());
        }
        
        // Guardar estilos
        try {
            StyledDocument doc2 = areaTexto.getStyledDocument();
            StringBuilder html = new StringBuilder();
            for (int i = 0; i < doc2.getLength(); i++) {
                AttributeSet attrs = doc2.getCharacterElement(i).getAttributes();
                Object color = attrs.getAttribute(StyleConstants.Foreground);
                html.append(color != null ? color.toString() : "default");
            }
            estilosDocumentos.put(documentoActualTitulo, html.toString());
        } catch (Exception ex) { /* ignore */ }
    }

    private void restaurarEstilosDocumento(String titulo) {
        // Limpiar estilos previos
        StyledDocument doc = areaTexto.getStyledDocument();
        Style defaultStyle = areaTexto.getStyle(StyleContext.DEFAULT_STYLE);
        doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, true);
        
        // Reaplicar etiquetas del documento actual (solo si aparecen en el texto)
        if (ctrl != null && ctrl.getProyecto() != null) {
            String texto = areaTexto.getText();
            for (Etiqueta e : ctrl.getProyecto().getEtiquetas()) {
                if (e == null || e.getFragmentos() == null) continue;
                for (Fragmento f : e.getFragmentos()) {
                    if (f != null && f.getTexto() != null && texto.contains(f.getTexto())) {
                        colorearTexto(f.getTexto(), e.getColor());
                    }
                }
            }
        }
    }

    private void actualizarAnalisisDocumento() {
        // reiniciar controlador de análisis (refresca con el proyecto actual)
        this.ctrlAnalisis = new ControladorAnalisis(ctrl.getProyecto());
    }

    private void actualizarContadoresDocumento() {
        // palabras del documento abierto
        String contenido = areaTexto.getText();
        int palabras = contarPalabrasFragmento(contenido);
        lblPalabras.setText("Palabras: " + palabras);

        // etiquetas aplicadas en este documento: contar fragmentos cuya texto aparece en el documento actual
        int contadorEtiq = 0;
        if (ctrl != null && ctrl.getProyecto() != null) {
            String texto = contenido == null ? "" : contenido;
            for (Etiqueta e : ctrl.getProyecto().getEtiquetas()) {
                if (e == null || e.getFragmentos() == null) continue;
                for (Fragmento f : e.getFragmentos()) {
                    if (f != null && f.getTexto() != null && texto.contains(f.getTexto())) {
                        contadorEtiq++;
                        break; // contar etiqueta una vez si aparece
                    }
                }
            }
        }
        lblEtiquetas.setText("Etiquetas en doc: " + contadorEtiq);
    }

    private void eliminarDocumento() {
        int idx = listaDocumentos.getSelectedIndex();
        if (idx >= 0) {
            String titulo = modeloDocumentos.get(idx);
            ctrl.eliminarDocumento(titulo);
            actualizarListaDocumentos();
            areaTexto.setText("");
            documentoActualTitulo = "";
            estilosDocumentos.remove(titulo);
            
            // Limpiar análisis del documento eliminado
            if (ctrlAnalisis != null) {
                ctrlAnalisis = new ControladorAnalisis(ctrl.getProyecto());
            }
            actualizarContadoresDocumento();
        }
    }

    private void crearNuevoDocumentoDialog() {
        JDialog dialog = new JDialog(this, "Crear nuevo documento", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);

        JTextField txtTitulo = new JTextField();
        JTextField txtAutor = new JTextField();
        txtNuevoDocumento = new JTextArea();
        txtNuevoDocumento.setFont(new Font("Arial", Font.PLAIN, 14));
        txtNuevoDocumento.setLineWrap(true);
        txtNuevoDocumento.setWrapStyleWord(true);

        JPanel contentPane = new JPanel(new BorderLayout());
        JPanel top = new JPanel(new GridLayout(2, 2, 4, 4));
        top.add(new JLabel("Título:"));
        top.add(txtTitulo);
        top.add(new JLabel("Autor:"));
        top.add(txtAutor);

        contentPane.add(top, BorderLayout.NORTH);
        contentPane.add(new JScrollPane(txtNuevoDocumento), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnOK = new JButton("Crear");
        JButton btnCancel = new JButton("Cancelar");

        btnOK.addActionListener(e -> {
            String titulo = txtTitulo.getText().trim();
            String autor = txtAutor.getText().trim();
            String contenido = txtNuevoDocumento.getText();
            if (!titulo.isEmpty()) {
                Documento doc = new Transcripcion(titulo, autor.isEmpty() ? "Desconocido" : autor, contenido);
                ctrl.getProyecto().agregarDocumento(doc);
                actualizarListaDocumentos();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "El título no puede estar vacío");
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        buttons.add(btnOK);
        buttons.add(btnCancel);

        contentPane.add(buttons, BorderLayout.SOUTH);
        dialog.setContentPane(contentPane);
        dialog.setVisible(true);
    }

    private void crearEtiquetaDialog() {
        JTextField nombre = new JTextField();
        JTextField desc = new JTextField();
        JComboBox<String> colorOptions = new JComboBox<>(new String[]{"red", "blue", "green", "yellow", "orange", "purple", "magenta", "cyan", "gray", "Custom (#HEX)"});
        JTextField customColor = new JTextField("#FF6600");
        Object[] message = {"Nombre:", nombre, "Descripción:", desc, "Color:", colorOptions, "Color custom (si aplica):", customColor};
        int option = JOptionPane.showConfirmDialog(this, message, "Nueva etiqueta", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION && !nombre.getText().trim().isEmpty()) {
            String chosen = (String) colorOptions.getSelectedItem();
            String finalColor = chosen;
            if ("Custom (#HEX)".equals(chosen)) finalColor = customColor.getText().trim();
            ctrl.crearEtiqueta(nombre.getText().trim(), desc.getText().trim(), finalColor);
            actualizarCodebook();
        }
    }

    private void aplicarEtiquetaDialog() {
        String selectedText = areaTexto.getSelectedText();
        if (selectedText == null || selectedText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Seleccione un fragmento de texto");
            return;
        }
        
        if (documentoActualTitulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, abra un documento primero");
            return;
        }

        List<Etiqueta> etiquetas = ctrl.getProyecto().getEtiquetas();
        if (etiquetas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay etiquetas. Cree una primero.");
            return;
        }
        String[] nombres = etiquetas.stream().map(Etiqueta::getNombre).toArray(String[]::new);
        String sel = (String) JOptionPane.showInputDialog(this, "Seleccione etiqueta:", "Aplicar etiqueta",
                JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);
        if (sel != null) {
            Etiqueta e = ctrl.getProyecto().buscarEtiquetaPorNombre(sel);
            if (e != null) {
                Fragmento f = new Fragmento(selectedText, e);
                e.agregarFragmento(f);

                colorearTexto(selectedText, e.getColor());

                // guardar y actualizar: contadores y análisis
                guardarDocumentoActual();
                actualizarContadoresDocumento();
                actualizarAnalisisDocumento();

                JOptionPane.showMessageDialog(this, "Etiqueta aplicada correctamente");
            }
        }
    }

    private void descargarAnalisisDebajo() {
        if (documentoActualTitulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Abra primero el documento cuyo análisis desea exportar.");
            return;
        }
        Documento doc = ctrl.getProyecto().buscarDocumentoPorTitulo(documentoActualTitulo);
        if (doc == null) {
            JOptionPane.showMessageDialog(this, "Documento no encontrado.");
            return;
        }

        String suggested = doc.getTitulo() + "_analisis.txt";
        JFileChooser fc;
        if (doc.getSourcePath() != null) {
            File src = new File(doc.getSourcePath());
            File parent = src.getParentFile();
            fc = new JFileChooser(parent);
            fc.setSelectedFile(new File(parent, suggested));
        } else {
            fc = new JFileChooser();
            fc.setSelectedFile(new File(suggested));
        }
        fc.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            String path = f.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".txt")) path += ".txt";
            try {
                if (ctrlAnalisis == null) ctrlAnalisis = new ControladorAnalisis(ctrl.getProyecto());
                ctrlAnalisis.exportarAnalisisPlano(path, areaTexto.getText(), ctrl.getProyecto(), ctrl.getProyecto().getHipotesis());
                JOptionPane.showMessageDialog(this, "Análisis exportado en:\n" + path);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al exportar análisis: " + ex.getMessage());
            }
        }
    }

    private Color parseColorString(String colorStr) {
        if (colorStr == null) return new Color(64, 64, 64);
        try {
            if (colorStr.startsWith("#")) return Color.decode(colorStr);
            switch (colorStr.toLowerCase()) {
                case "red": return Color.RED;
                case "blue": return Color.BLUE;
                case "green": return Color.GREEN;
                case "yellow": return Color.YELLOW;
                case "orange": return Color.ORANGE;
                case "purple": return new Color(0x800080);
                case "magenta": return Color.MAGENTA;
                case "cyan": return Color.CYAN;
                case "gray": return Color.GRAY;
                default:
                    if (colorStr.matches("^[0-9a-fA-F]{6}$")) return Color.decode("#" + colorStr);
            }
        } catch (Exception ex) { /* ignore */ }
        return new Color(64, 64, 64);
    }

    private void colorearTexto(String fragmento, String color) {
        try {
            StyledDocument doc = areaTexto.getStyledDocument();
            String texto = areaTexto.getText();
            int index = texto.indexOf(fragmento);
            if (index >= 0) {
                Style style = areaTexto.addStyle("Color" + System.nanoTime(), null);
                Color c = parseColorString(color);
                StyleConstants.setForeground(style, c);
                doc.setCharacterAttributes(index, fragmento.length(), style, false);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void crearHipotesisDialog() {
        JTextArea area = new JTextArea(6, 40);
        area.setFont(new Font("Arial", Font.PLAIN, 16));
        if (!hipotesisActual.isEmpty()) area.setText(hipotesisActual);
        int opt = JOptionPane.showConfirmDialog(this, new JScrollPane(area),
                "Escribe la Hipótesis", JOptionPane.OK_CANCEL_OPTION);
        if (opt == JOptionPane.OK_OPTION) {
            hipotesisActual = area.getText().trim();
            ctrl.crearHipotesis(hipotesisActual);
            lblHipotesis.setText("Hipótesis: " + hipotesisActual);
        }
    }

    private void mostrarAnalisisCompleto() {
        // asegurar análisis actualizado antes de mostrar
        actualizarAnalisisDocumento();

        int palabras = ctrlAnalisis.contarPalabrasTotales();
        int etiquetas = ctrlAnalisis.contarEtiquetas();
        int fragmentos = ctrl.getProyecto().getEtiquetas().stream().mapToInt(Etiqueta::contarFragmentos).sum();

        Map<String, Integer> palabrasPorEtiqueta = ctrlAnalisis.contarPalabrasEtiqueta();

        // tomar hipótesis del proyecto (no de una etiqueta visible)
        List<String> hipotesis = ctrl.getProyecto().getHipotesis();

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family:Arial; font-size:16px; background:#f5f5f5;'>");
        sb.append("<h2 style='color:#333;'>").append(ctrl.getProyecto().getNombre()).append("</h2>");
        sb.append("<h3 style='color:#555;'>Hipótesis</h3><p style='color:#666;'>")
          .append(hipotesis == null || hipotesis.isEmpty() ? "(ninguna)" : String.join("<br>", hipotesis))
          .append("</p>");
        sb.append("<h3 style='color:#555;'>Resumen General</h3>");
        sb.append("<p style='color:#666;'><strong>Palabras totales:</strong> ").append(palabras);
        sb.append("<br><strong>Etiquetas:</strong> ").append(etiquetas);
        sb.append("<br><strong>Fragmentos etiquetados:</strong> ").append(fragmentos).append("</p>");
        
        sb.append("<h3 style='color:#555;'>Palabras por Etiqueta</h3>");
        for (Map.Entry<String, Integer> entry : palabrasPorEtiqueta.entrySet()) {
            sb.append("<p style='color:#666;'><strong>").append(entry.getKey()).append(":</strong> ").append(entry.getValue()).append(" palabras</p>");
        }

        sb.append("<h3 style='color:#555;'>Resumen por Etiqueta</h3>");
        for (Etiqueta e : ctrl.getProyecto().getEtiquetas()) {
            sb.append("<p><span style='color:").append(e.getColor()).append("; font-weight:bold;'>■ ")
              .append(e.getNombre()).append("</span>: ")
              .append(e.contarFragmentos()).append(" fragmentos</p>");
        }
        sb.append("</body></html>");

        JEditorPane editor = new JEditorPane("text/html", sb.toString());
        editor.setEditable(false);
        JScrollPane scroll = new JScrollPane(editor);
        scroll.setPreferredSize(new Dimension(700, 600));
        JOptionPane.showMessageDialog(this, scroll, "Análisis de Resultados", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarAnalisisEtiquetaSeleccionada() {
        int idx = listEtiquetas.getSelectedIndex();
        if (idx < 0) { JOptionPane.showMessageDialog(this, "Seleccione una etiqueta"); return; }
        String nombre = modeloEtiquetas.get(idx);
        Etiqueta e = ctrl.getProyecto().buscarEtiquetaPorNombre(nombre);
        if (e == null) return;
        
        int palabrasTotalesEtiqueta = ctrlAnalisis.contarPalabrasEtiqueta().getOrDefault(e.getNombre(), 0);
        
        StringBuilder sb = new StringBuilder("<html><body style='font-family:Arial; font-size:14px;'>");
        sb.append("<h2>").append(e.getNombre()).append("</h2>");
        sb.append("<p>").append(e.getDescripcion() == null ? "" : e.getDescripcion()).append("</p>");
        sb.append("<p><strong>Palabras totales: ").append(palabrasTotalesEtiqueta).append("</strong></p>");
        sb.append("<p><strong>Fragmentos: ").append(e.contarFragmentos()).append("</strong></p>");
        sb.append("<h3>Texto etiquetado (con conteo por fragmento)</h3>");
        for (Fragmento f : e.getFragmentos()) {
            int palabrasFragmento = contarPalabrasFragmento(f.getTexto());
            sb.append("<p>• ").append(f.getTexto()).append(" <em>[").append(palabrasFragmento).append(" palabras]</em></p>");
        }
        sb.append("</body></html>");
        JEditorPane ed = new JEditorPane("text/html", sb.toString());
        ed.setEditable(false);
        JScrollPane scroll = new JScrollPane(ed);
        scroll.setPreferredSize(new Dimension(700, 500));
        JOptionPane.showMessageDialog(this, scroll, "Análisis por Etiqueta", JOptionPane.INFORMATION_MESSAGE);
    }

    private int contarPalabrasFragmento(String texto) {
        if (texto == null || texto.trim().isEmpty()) return 0;
        String[] palabras = texto.split("\\W+");
        int count = 0;
        for (String p : palabras) {
            if (p.trim().length() > 0) count++;
        }
        return count;
    }

    private void editarEtiquetaDialog() {
        int idx = listEtiquetas.getSelectedIndex();
        if (idx < 0) { JOptionPane.showMessageDialog(this, "Seleccione una etiqueta"); return; }
        String oldName = modeloEtiquetas.get(idx);
        Etiqueta e = ctrl.getProyecto().buscarEtiquetaPorNombre(oldName);
        if (e == null) return;

        JTextField nombre = new JTextField(e.getNombre());
        JTextField desc = new JTextField(e.getDescripcion() == null ? "" : e.getDescripcion());
        JComboBox<String> colorOptions = new JComboBox<>(new String[]{"red", "blue", "green", "yellow", "orange", "purple", "magenta", "cyan", "gray", "Custom (#HEX)"});
        JTextField customColor = new JTextField(e.getColor() == null ? "#FF6600" : e.getColor());
        
        String col = e.getColor();
        if (col != null) {
            boolean matched = false;
            for (int i = 0; i < colorOptions.getItemCount(); i++) {
                if (colorOptions.getItemAt(i).equalsIgnoreCase(col)) { colorOptions.setSelectedIndex(i); matched = true; break; }
            }
            if (!matched && col.startsWith("#")) colorOptions.setSelectedItem("Custom (#HEX)");
        }

        Object[] message = {"Nombre:", nombre, "Descripción:", desc, "Color:", colorOptions, "Color custom (si aplica):", customColor};
        int option = JOptionPane.showConfirmDialog(this, message, "Editar etiqueta", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION && !nombre.getText().trim().isEmpty()) {
            String chosen = (String) colorOptions.getSelectedItem();
            String finalColor = chosen;
            if ("Custom (#HEX)".equals(chosen)) finalColor = customColor.getText().trim();
            ctrl.editarEtiqueta(oldName, nombre.getText().trim(), desc.getText().trim(), finalColor);
            actualizarCodebook();
        }
    }

    private void resumenEtiquetaDialog() {
        StringBuilder sb = new StringBuilder("<html><body style='font-family:Arial; font-size:16px;'>");
        for (Etiqueta e : ctrl.getProyecto().getEtiquetas()) {
            sb.append("<h3>").append(e.getNombre()).append("</h3>");
            for (Fragmento f : e.getFragmentos()) sb.append("<p>• ").append(f.getTexto()).append("</p>");
        }
        sb.append("</body></html>");
        JEditorPane ed = new JEditorPane("text/html", sb.toString());
        ed.setEditable(false);
        JScrollPane scroll = new JScrollPane(ed);
        scroll.setPreferredSize(new Dimension(700, 500));
        JOptionPane.showMessageDialog(this, scroll, "Resumen de Etiquetas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void descargarDocumento() {
        if (documentoActualTitulo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un documento primero.");
            return;
        }

        Documento doc = ctrl.getProyecto().buscarDocumentoPorTitulo(documentoActualTitulo);
        if (doc == null) { JOptionPane.showMessageDialog(this, "Documento no encontrado."); return; }

        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        if (doc.getSourcePath() != null) {
            File src = new File(doc.getSourcePath());
            fc.setCurrentDirectory(src.getParentFile());
            fc.setSelectedFile(new File(src.getParentFile(), doc.getTitulo() + ".txt"));
        } else {
            fc.setSelectedFile(new File(doc.getTitulo() + ".txt"));
        }

        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            String path = f.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".txt")) path += ".txt";
            try (FileWriter fw = new FileWriter(path)) {
                // escribir contenido del documento y, sólo aquí, las hipótesis al principio
                List<String> hipotesis = ctrl.getProyecto().getHipotesis();
                if (hipotesis != null && !hipotesis.isEmpty()) {
                    fw.write("Hipótesis:\n");
                    for (String h : hipotesis) fw.write("- " + h + "\n");
                    fw.write("\n---\n\n");
                }
                fw.write(areaTexto.getText());
                JOptionPane.showMessageDialog(this, "Documento descargado correctamente en:\n" + path);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al descargar documento: " + e.getMessage());
            }
        }
    }

    private void guardarProyectoDialog() {
        // Guardar documento actual antes de guardar proyecto
        if (!documentoActualTitulo.isEmpty()) {
            guardarDocumentoActual();
        }

        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("Proyecto Files", "proj"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            String path = f.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".proj")) path += ".proj";
            try {
                ctrl.guardarProyecto(new File(path));
                JOptionPane.showMessageDialog(this, "Proyecto guardado correctamente en:\n" + path);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al guardar proyecto: " + e.getMessage());
            }
        }
    }

    // agrega/actualiza la lista de documentos en la UI
    public void actualizarListaDocumentos() {
        if (modeloDocumentos == null) return;
        modeloDocumentos.clear();
        if (ctrl == null || ctrl.getProyecto() == null) return;
        List<Documento> docs = ctrl.getProyecto().getDocumentos();
        if (docs == null) return;
        for (Documento d : docs) {
            if (d != null) modeloDocumentos.addElement(d.getTitulo());
        }
    }

    // agrega/actualiza el codebook (lista de etiquetas) en la UI
    public void actualizarCodebook() {
        if (modeloEtiquetas == null) return;
        modeloEtiquetas.clear();
        if (ctrl == null || ctrl.getProyecto() == null) return;
        List<Etiqueta> etqs = ctrl.getProyecto().getEtiquetas();
        if (etqs == null) return;
        for (Etiqueta e : etqs) {
            if (e != null) modeloEtiquetas.addElement(e.getNombre());
        }
        // al cambiar codebook actualizamos contadores y también el análisis
        actualizarContadoresDocumento();
        actualizarAnalisisDocumento();
    }

    // Icon cuadrado de color para el renderer
    private static class ColorIcon implements Icon {
        private final Color color;
        private final int size = 12;
        public ColorIcon(Color c) { this.color = c; }
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            if (color != null) {
                g.setColor(color);
                g.fillRect(x, y, size, size);
                g.setColor(Color.DARK_GRAY);
                g.drawRect(x, y, size, size);
            }
        }
        @Override public int getIconWidth() { return size; }
        @Override public int getIconHeight() { return size; }
    }
}
