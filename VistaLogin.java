import java.awt.*;
import javax.swing.*;

public class VistaLogin extends JFrame {
    private JComboBox<String> tipoUsuario;
    private JTextField txtNombre;
    private VistaPrincipal vistaPrincipal;
    private ControladorPrincipal controlador;

    public VistaLogin() {
        super("Anthro-Analyzer Pro - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(950, 1050);
        setLocationRelativeTo(null);
        setResizable(false);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 240, 240));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;

        // Título principal
        JLabel lblTitulo = new JLabel("Anthro-Analyzer Pro");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 26));
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.weighty = 0;
        panel.add(lblTitulo, gbc);

        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Sistema de Análisis Antropológico");
        lblSubtitulo.setFont(new Font("Arial", Font.ITALIC, 12));
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 10, 10);
        panel.add(lblSubtitulo, gbc);

        // Separador
        JSeparator sep1 = new JSeparator();
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 10, 10, 10);
        panel.add(sep1, gbc);

        // Panel de entrada (Nombre y Tipo de usuario)
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(240, 240, 240));
        GridBagConstraints gbcInput = new GridBagConstraints();
        gbcInput.fill = GridBagConstraints.HORIZONTAL;
        gbcInput.insets = new Insets(5, 10, 5, 10);

        // Nombre de usuario
        JLabel lblNombre = new JLabel("Nombre:");
        lblNombre.setFont(new Font("Arial", Font.PLAIN, 12));
        gbcInput.gridx = 0;
        gbcInput.gridy = 0;
        gbcInput.weightx = 0.2;
        inputPanel.add(lblNombre, gbcInput);

        txtNombre = new JTextField(20);
        txtNombre.setFont(new Font("Arial", Font.PLAIN, 12));
        txtNombre.setPreferredSize(new Dimension(250, 30));
        gbcInput.gridx = 1;
        gbcInput.weightx = 0.8;
        inputPanel.add(txtNombre, gbcInput);

        // Tipo de usuario
        JLabel lblTipo = new JLabel("Tipo de usuario:");
        lblTipo.setFont(new Font("Arial", Font.PLAIN, 12));
        gbcInput.gridx = 0;
        gbcInput.gridy = 1;
        gbcInput.weightx = 0.2;
        inputPanel.add(lblTipo, gbcInput);

        tipoUsuario = new JComboBox<>(new String[]{
            "Administrador",
            "Investigador",
            "Asistente",
            "Lector"
        });
        tipoUsuario.setFont(new Font("Arial", Font.PLAIN, 12));
        tipoUsuario.setPreferredSize(new Dimension(250, 30));
        gbcInput.gridx = 1;
        gbcInput.weightx = 0.8;
        inputPanel.add(tipoUsuario, gbcInput);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(inputPanel, gbc);

        // Panel de permisos - OCUPA TODO EL ESPACIO
        JPanel permisosPanel = new JPanel();
        permisosPanel.setLayout(new BoxLayout(permisosPanel, BoxLayout.Y_AXIS));
        permisosPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 3),
            "Permisos y Funcionalidades",
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 16)
        ));
        permisosPanel.setBackground(new Color(255, 255, 255));

        JTextArea txtPermisos = new JTextArea(40, 120);
        txtPermisos.setEditable(false);
        txtPermisos.setFont(new Font("Courier New", Font.PLAIN, 13));
        txtPermisos.setText(obtenerPermisosTexto("Administrador"));
        txtPermisos.setLineWrap(true);
        txtPermisos.setWrapStyleWord(true);
        txtPermisos.setMargin(new Insets(20, 20, 20, 20));
        txtPermisos.setBackground(new Color(250, 250, 255));
        
        JScrollPane scrollPermisos = new JScrollPane(txtPermisos);
        permisosPanel.add(scrollPermisos);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(10, 10, 10, 10);
        panel.add(permisosPanel, gbc);

        // Actualizar permisos al cambiar rol
        tipoUsuario.addActionListener(e -> 
            txtPermisos.setText(obtenerPermisosTexto((String) tipoUsuario.getSelectedItem()))
        );

        // Botones de acción
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(new Color(240, 240, 240));
        
        JButton btnIngresar = new JButton("Ingresar");
        btnIngresar.setFont(new Font("Arial", Font.BOLD, 13));
        btnIngresar.setPreferredSize(new Dimension(140, 45));
        btnIngresar.setBackground(new Color(70, 130, 180));
        btnIngresar.setForeground(Color.WHITE);
        btnIngresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnIngresar.addActionListener(e -> ingresar());

        JButton btnSalir = new JButton("Salir");
        btnSalir.setFont(new Font("Arial", Font.BOLD, 13));
        btnSalir.setPreferredSize(new Dimension(140, 45));
        btnSalir.setBackground(new Color(200, 70, 70));
        btnSalir.setForeground(Color.WHITE);
        btnSalir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSalir.addActionListener(e -> System.exit(0));

        btnPanel.add(btnIngresar);
        btnPanel.add(btnSalir);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.insets = new Insets(10, 10, 15, 10);
        panel.add(btnPanel, gbc);

        add(panel);
    }

    private String obtenerPermisosTexto(String tipo) {
        switch (tipo) {
            case "Administrador":
                return "╔═══════════════════ ADMINISTRADOR - ACCESO COMPLETO ═══════════════════╗\n\n" +
                       "📄 GESTIÓN DE DOCUMENTOS:\n" +
                       "   ✓ Crear nuevos documentos\n" +
                       "   ✓ Editar documentos existentes\n" +
                       "   ✓ Cargar archivos de texto (.txt)\n" +
                       "   ✓ Limpiar contenido de documentos\n" +
                       "   ✓ Eliminar documentos\n\n" +
                       "🏷️  GESTIÓN DE ETIQUETAS:\n" +
                       "   ✓ Crear nuevas etiquetas/categorías\n" +
                       "   ✓ Editar etiquetas existentes\n" +
                       "   ✓ Aplicar etiquetas a fragmentos de texto\n" +
                       "   ✓ Ver todas las etiquetas en el codebook\n" +
                       "   ✓ Eliminar etiquetas\n\n" +
                       "📊 ANÁLISIS Y REPORTES:\n" +
                       "   ✓ Ver análisis completo del proyecto\n" +
                       "   ✓ Ver análisis por etiqueta individual\n" +
                       "   ✓ Descargar análisis en formato texto\n" +
                       "   ✓ Ver resumen de etiquetas\n" +
                       "   ✓ Ver estadísticas detalladas\n\n" +
                       "📋 GESTIÓN DEL PROYECTO:\n" +
                       "   ✓ Crear y editar hipótesis de investigación\n" +
                       "   ✓ Guardar proyectos completos\n" +
                       "   ✓ Cargar proyectos guardados\n" +
                       "   ✓ Acceso sin restricciones a todas las funciones\n\n" +
                       "╚═══════════════════════════════════════════════════════════════════════╝";
                
            case "Investigador":
                return "╔═══════════════════ INVESTIGADOR - ACCESO AMPLIO ══════════════════╗\n\n" +
                       "📄 GESTIÓN DE DOCUMENTOS:\n" +
                       "   ✓ Crear nuevos documentos\n" +
                       "   ✓ Editar documentos existentes\n" +
                       "   ✓ Cargar archivos de texto (.txt)\n" +
                       "   ✓ Limpiar contenido de documentos\n" +
                       "   ✓ Eliminar documentos\n\n" +
                       "🏷️  GESTIÓN DE ETIQUETAS:\n" +
                       "   ✓ Crear nuevas etiquetas/categorías\n" +
                       "   ✓ Editar etiquetas existentes\n" +
                       "   ✓ Aplicar etiquetas a fragmentos de texto\n" +
                       "   ✓ Ver todas las etiquetas en el codebook\n\n" +
                       "📊 ANÁLISIS Y REPORTES:\n" +
                       "   ✓ Ver análisis completo del proyecto\n" +
                       "   ✓ Ver análisis por etiqueta individual\n" +
                       "   ✓ Descargar análisis en formato texto\n" +
                       "   ✓ Ver resumen de etiquetas\n\n" +
                       "📋 GESTIÓN DEL PROYECTO:\n" +
                       "   ✓ Crear y editar hipótesis de investigación\n" +
                       "   ✓ Guardar proyectos propios\n" +
                       "   ✗ NO puede cargar proyectos de otros usuarios\n" +
                       "   ✓ Acceso a funciones de edición completas\n\n" +
                       "╚═════════════════════════════════════════════════════════════════╝";
                
            case "Asistente":
                return "╔════════════════════ ASISTENTE - ACCESO LIMITADO ═══════════════════╗\n\n" +
                       "📄 GESTIÓN DE DOCUMENTOS:\n" +
                       "   ✓ Ver documentos del proyecto (lectura)\n" +
                       "   ✓ Cargar archivos de texto (.txt)\n" +
                       "   ✗ NO puede crear nuevos documentos\n" +
                       "   ✗ NO puede editar documentos existentes\n" +
                       "   ✗ NO puede eliminar documentos\n\n" +
                       "🏷️  GESTIÓN DE ETIQUETAS:\n" +
                       "   ✓ Aplicar etiquetas a fragmentos de texto\n" +
                       "   ✓ Ver todas las etiquetas en el codebook\n" +
                       "   ✗ NO puede crear nuevas etiquetas\n" +
                       "   ✗ NO puede editar etiquetas existentes\n" +
                       "   ✗ NO puede eliminar etiquetas\n\n" +
                       "📊 ANÁLISIS Y REPORTES:\n" +
                       "   ✓ Ver análisis por etiqueta individual\n" +
                       "   ✓ Ver resumen de etiquetas\n" +
                       "   ✗ NO puede descargar análisis completos\n" +
                       "   ✗ NO puede ver análisis global del proyecto\n\n" +
                       "📋 GESTIÓN DEL PROYECTO:\n" +
                       "   ✓ Ver hipótesis del proyecto\n" +
                       "   ✗ NO puede crear o editar hipótesis\n" +
                       "   ✗ NO puede guardar proyectos\n" +
                       "   ✗ NO puede cargar proyectos\n\n" +
                       "╚═════════════════════════════════════════════════════════════════╝";
                
            case "Lector":
                return "╔══════════════════════ LECTOR - ACCESO MÍNIMO ════════════════════════╗\n\n" +
                       "📄 GESTIÓN DE DOCUMENTOS:\n" +
                       "   ✓ Ver documentos del proyecto (solo lectura)\n" +
                       "   ✓ Cargar archivos de texto (.txt)\n" +
                       "   ✗ NO puede crear nuevos documentos\n" +
                       "   ✗ NO puede editar documentos\n" +
                       "   ✗ NO puede eliminar documentos\n\n" +
                       "🏷️  GESTIÓN DE ETIQUETAS:\n" +
                       "   ✓ Ver todas las etiquetas en el codebook\n" +
                       "   ✓ Ver análisis por etiqueta individual\n" +
                       "   ✗ NO puede aplicar etiquetas\n" +
                       "   ✗ NO puede crear nuevas etiquetas\n" +
                       "   ✗ NO puede editar etiquetas\n\n" +
                       "📊 ANÁLISIS Y REPORTES:\n" +
                       "   ✓ Ver análisis por etiqueta individual\n" +
                       "   ✓ Ver resumen de etiquetas\n" +
                       "   ✓ Ver hipótesis del proyecto\n" +
                       "   ✗ NO puede descargar análisis\n" +
                       "   ✗ NO puede ver análisis completo\n\n" +
                       "📋 GESTIÓN DEL PROYECTO:\n" +
                       "   ✗ NO puede crear o editar hipótesis\n" +
                       "   ✗ NO puede guardar proyectos\n" +
                       "   ✗ NO puede cargar proyectos\n" +
                       "   ✗ Acceso limitado solo a lectura\n\n" +
                       "╚══════════════════════════════════════════════════════════════════════╝";
                
            default:
                return "";
        }
    }

    private void ingresar() {
        String nombre = txtNombre.getText().trim();
        String tipo = (String) tipoUsuario.getSelectedItem();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, ingrese su nombre de usuario", 
                "Campo vacío", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Crear usuario
        Usuario usuario = new Usuario(nombre, "email@example.com", 1, tipo.toLowerCase(), "");

        // Crear vista principal con permisos
        vistaPrincipal = new VistaPrincipal(usuario);
        controlador = new ControladorPrincipal(vistaPrincipal);
        vistaPrincipal.setControlador(controlador);
        vistaPrincipal.setVisible(true);

        // Cerrar login
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new VistaLogin().setVisible(true);
        });
    }
}