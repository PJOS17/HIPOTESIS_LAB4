import java.awt.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;



public class VistaPrincipal extends JFrame {
    private ControladorPrincipal ctrl;
    private ControladorAnalisis ctrlAnalisis;
    private JList<String> listaDocumentos;
    private DefaultListModel<String> modeloDocumentos;
    private DefaultListModel<String> modeloEtiquetas;
    private JTextArea areaTexto;
    private JTextField txtProyecto;
    private Fragmento fragmento; 

    public VistaPrincipal(){
        super("Anthro-Analyzer (prototipo)");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100,700);
        setLocationRelativeTo(null);
        initComponents();
    }

    public void setControlador(ControladorPrincipal c) {
        if (c == null) {
            throw new IllegalArgumentException("Controlador no puede ser null");
        }
        this.ctrl = c;
        this.ctrlAnalisis = new ControladorAnalisis(c.getProyecto());
        refreshAll();
    }

    // Reemplazar el método refreshAll existente con esta versión mejorada
    public void refreshAll() {
        SwingUtilities.invokeLater(() -> {
            actualizarListaDocumentos();
            actualizarCodebook();
            if (ctrl != null && ctrl.getProyecto() != null) {
                txtProyecto.setText(ctrl.getProyecto().getNombre());
            }
            areaTexto.setText("");
        });
    }

    private void initComponents(){
        JPanel left = new JPanel();
        left.setLayout(new BorderLayout());
        left.setPreferredSize(new Dimension(260,600));

        JPanel topLeft = new JPanel();
        topLeft.setLayout(new FlowLayout());
        txtProyecto = new JTextField("Proyecto nuevo", 12);
        JButton btnGuardarNombre = new JButton("Guardar");
        btnGuardarNombre.addActionListener(e-> {
            ctrl.setNombreProyecto(txtProyecto.getText());
        });
        topLeft.add(new JLabel("Proyecto"));
        topLeft.add(txtProyecto);
        topLeft.add(btnGuardarNombre);

        modeloDocumentos = new DefaultListModel<>();
        listaDocumentos = new JList<>(modeloDocumentos);
        listaDocumentos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollDocs = new JScrollPane(listaDocumentos);

        JButton btnCargar = new JButton("Cargar texto");
        btnCargar.addActionListener(e-> {
            JFileChooser fc = new JFileChooser();
            if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                ctrl.agregarDocumentoDesdeArchivo(f);
            }
        });

        JButton btnAbrir = new JButton("Abrir");
        btnAbrir.addActionListener(e-> abrirDocumentoSeleccionado());

        JButton btnSaveProject = new JButton("Guardar proyecto");
        btnSaveProject.addActionListener(e-> {
            JFileChooser fc = new JFileChooser();
            if(fc.showSaveDialog(this)==JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                ctrl.guardarProyecto(f);
            }
        });

        JButton btnLoadProject = new JButton("Cargar proyecto");
        btnLoadProject.addActionListener(e-> {
            JFileChooser fc = new JFileChooser();
            if(fc.showOpenDialog(this)==JFileChooser.APPROVE_OPTION){
                File f = fc.getSelectedFile();
                ctrl.cargarProyecto(f);
                this.ctrlAnalisis = new ControladorAnalisis(ctrl.getProyecto());
            }
        });

        left.add(topLeft, BorderLayout.NORTH);
        JPanel leftButtons = new JPanel(new GridLayout(4,1,4,4));
        leftButtons.add(btnCargar);
        leftButtons.add(btnAbrir);
        leftButtons.add(btnSaveProject);
        leftButtons.add(btnLoadProject);
        left.add(leftButtons, BorderLayout.SOUTH);
        left.add(scrollDocs, BorderLayout.CENTER);

        // Center panel: documento y acciones
        JPanel center = new JPanel(new BorderLayout());
        areaTexto = new JTextArea();
        areaTexto.setLineWrap(true);
        areaTexto.setWrapStyleWord(true);
        JScrollPane scrollCenter = new JScrollPane(areaTexto);

        JPanel acciones = new JPanel(new FlowLayout());
        JButton btnCrearEtiqueta = new JButton("Crear etiqueta");
        btnCrearEtiqueta.addActionListener(e -> crearEtiquetaDialog());
        JButton btnAplicarEtiqueta = new JButton("Aplicar etiqueta");
        btnAplicarEtiqueta.addActionListener(e -> aplicarEtiquetaDialog());
        JButton btnResumen = new JButton("Resumen etiqueta");
        btnResumen.addActionListener(e -> resumenEtiquetaDialog());
        JButton btnExportCSV = new JButton("Exportar codificaciones (CSV)");
        btnExportCSV.addActionListener(e -> exportarCSV());

        acciones.add(btnCrearEtiqueta);
        acciones.add(btnAplicarEtiqueta);
        acciones.add(btnResumen);
        acciones.add(btnExportCSV);

        center.add(acciones, BorderLayout.NORTH);
        center.add(scrollCenter, BorderLayout.CENTER);

        // Right: codebook
        JPanel right = new JPanel(new BorderLayout());
        right.setPreferredSize(new Dimension(320,600));
        modeloEtiquetas = new DefaultListModel<>();
        JList<String> listEtiquetas = new JList<>(modeloEtiquetas);
        JScrollPane scrollEtiq = new JScrollPane(listEtiquetas);
        right.add(new JLabel("Codebook"), BorderLayout.NORTH);
        right.add(scrollEtiq, BorderLayout.CENTER);

        JButton btnVer = new JButton("Ver detalles");
        btnVer.addActionListener(e -> {
            String sel = listEtiquetas.getSelectedValue();
            if(sel!=null){
                Etiqueta et = ctrl.getProyecto().buscarEtiquetaPorNombre(sel);
                if(et!=null){
                    StringBuilder sb = new StringBuilder();
                    sb.append("Etiqueta: ").append(et.getNombre()).append("\n")
                      .append("Descripción: ").append(et.getDescripcion()).append("\n")
                      .append("Fragmentos: \n");
                    for (Fragmento f : et.getFragmentos()) {
                        sb.append("- ").append(f.getTexto()).append("\n");
                    }
                    JOptionPane.showMessageDialog(this, sb.toString());
                }
            }
        });

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.addActionListener(e -> {
            String sel = listEtiquetas.getSelectedValue();
            if(sel!=null){
                Etiqueta et = ctrl.getProyecto().buscarEtiquetaPorNombre(sel);
                if(et!=null){
                    ctrl.getProyecto().getEtiquetas().remove(et);
                    actualizarCodebook();
                }
            }
        });

        JPanel btnsRight = new JPanel(new FlowLayout());
        btnsRight.add(btnVer);
        btnsRight.add(btnEliminar);
        right.add(btnsRight, BorderLayout.SOUTH);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(left, BorderLayout.WEST);
        getContentPane().add(center, BorderLayout.CENTER);
        getContentPane().add(right, BorderLayout.EAST);
    }

    private void abrirDocumentoSeleccionado() {
        if (ctrl == null || ctrl.getProyecto() == null) {
            JOptionPane.showMessageDialog(this, 
                "No hay proyecto abierto", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idx = listaDocumentos.getSelectedIndex();
        if (idx >= 0 && idx < ctrl.getProyecto().getDocumentos().size()) {
            Documento d = ctrl.getProyecto().getDocumentos().get(idx);
            if (d != null) {
                areaTexto.setText(d.getContenido());
                areaTexto.setCaretPosition(0);
            }
        }
    }

    public void actualizarListaDocumentos() {
        modeloDocumentos.clear();
        if (ctrl != null && ctrl.getProyecto() != null && ctrl.getProyecto().getDocumentos() != null) {
            for (Documento d : ctrl.getProyecto().getDocumentos()) {
                if (d != null && d.getTitulo() != null) {
                    modeloDocumentos.addElement(d.getTitulo());
                }
            }
        }
    }

    public void actualizarCodebook() {
        modeloEtiquetas.clear();
        if (ctrl != null && ctrl.getProyecto() != null && ctrl.getProyecto().getEtiquetas() != null) {
            for (Etiqueta e : ctrl.getProyecto().getEtiquetas()) {
                if (e != null && e.getNombre() != null) {
                    modeloEtiquetas.addElement(e.getNombre());
                }
            }
        }
    }

    private void crearEtiquetaDialog() {
        if (ctrl == null || ctrl.getProyecto() == null) {
            JOptionPane.showMessageDialog(this,
                "No hay proyecto activo",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JTextField nombre = new JTextField();
        JTextField desc = new JTextField();
        JTextField color = new JTextField("blue"); // default color
        Object[] message = {
            "Nombre:", nombre,
            "Descripción:", desc,
            "Color:", color
        };
        
        int option = JOptionPane.showConfirmDialog(this, message, "Crear etiqueta", 
            JOptionPane.OK_CANCEL_OPTION);
            
        if (option == JOptionPane.OK_OPTION) {
            String nombreStr = nombre.getText().trim();
            if (nombreStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "El nombre no puede estar vacío",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            ctrl.crearEtiqueta(nombreStr, desc.getText().trim(), color.getText().trim());
            actualizarCodebook();
        }
    }

    private void aplicarEtiquetaDialog() {
        if (ctrl == null || ctrl.getProyecto() == null) {
            JOptionPane.showMessageDialog(this,
                "No hay proyecto activo",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Etiqueta> etiquetas = ctrl.getProyecto().getEtiquetas();
        if (etiquetas == null || etiquetas.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No hay etiquetas. Cree una primero.", 
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedText = areaTexto.getSelectedText();
        if (selectedText == null || selectedText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Seleccione primero un fragmento de texto en el área central.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] nombres = etiquetas.stream()
            .filter(e -> e != null && e.getNombre() != null)
            .map(Etiqueta::getNombre)
            .toArray(String[]::new);

        String sel = (String) JOptionPane.showInputDialog(this,
            "Seleccione etiqueta:",
            "Aplicar etiqueta",
            JOptionPane.PLAIN_MESSAGE,
            null,
            nombres,
            nombres.length > 0 ? nombres[0] : null);

        if (sel != null) {
            ctrl.aplicarEtiquetaAFragmento(sel, selectedText.trim());
            actualizarCodebook();
            JOptionPane.showMessageDialog(this, 
                "Etiqueta aplicada: " + sel,
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void resumenEtiquetaDialog(){
        List<Etiqueta> etiquetas = ctrl.getProyecto().getEtiquetas();
        if(etiquetas.isEmpty()){
            JOptionPane.showMessageDialog(this, "No hay etiquetas.");
            return;
        }
        String[] nombres = etiquetas.stream().map(Etiqueta::getNombre).toArray(String[]::new);
        String sel = (String) JOptionPane.showInputDialog(this, "Seleccione etiqueta:", "Resumen etiqueta", JOptionPane.PLAIN_MESSAGE, null, nombres, nombres[0]);
        if(sel!=null){
            ctrlAnalisis.generarResumen(sel);
            // mostramos en un dialog simple
            StringBuilder sb = new StringBuilder();
            Etiqueta e = ctrl.getProyecto().buscarEtiquetaPorNombre(sel);
            for (Fragmento f : e.getFragmentos()) {
                sb.append("- ").append(f.getTexto()).append("\n");
            }
            JTextArea t = new JTextArea(sb.toString());
            t.setEditable(false);
            JScrollPane sp = new JScrollPane(t);
            sp.setPreferredSize(new Dimension(600,300));
            JOptionPane.showMessageDialog(this, sp, "Resumen: " + sel, JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void exportarCSV() {
        if (ctrl == null || ctrlAnalisis == null) {
            JOptionPane.showMessageDialog(this, 
                "No hay proyecto activo", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter(
            "Archivos CSV (*.csv)", "csv"));
            
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            String path = f.getAbsolutePath();
            if (!path.toLowerCase().endsWith(".csv")) {
                path += ".csv";
            }
            
            try {
                ctrlAnalisis.exportarCodificacionesCSV(path);
                JOptionPane.showMessageDialog(this, 
                    "Archivo exportado exitosamente:\n" + path, 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error al exportar: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }

    // API para acceder al controlador (desde Main)
    public void setCtrl(ControladorPrincipal controller) {
        setControlador(controller);
    }
}
