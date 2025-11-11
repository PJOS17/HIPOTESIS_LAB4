import java.io.*;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

public class ControladorPrincipal {
    private Proyecto proyecto;
    private VistaPrincipal vista;

    public ControladorPrincipal(VistaPrincipal vista){
        this.vista = vista;
        this.proyecto = new Proyecto("Proyecto nuevo");
    }

    public Proyecto getProyecto(){ return proyecto; }

    public void setNombreProyecto(String nombre){
        proyecto.setNombre(nombre);
        vista.actualizarListaDocumentos();
        vista.actualizarCodebook();
    }

    public void agregarDocumentoDesdeArchivo(File file){
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while((line=br.readLine())!=null){ sb.append(line).append("\n"); }
            br.close();
            Documento d = new Transcripcion(file.getName(), "Autor", sb.toString());
            d.setSourcePath(file.getAbsolutePath()); // <-- guardar ruta origen
            proyecto.agregarDocumento(d);
            vista.actualizarListaDocumentos();
            JOptionPane.showMessageDialog(vista, "Documento cargado: " + file.getName());
        } catch (IOException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al cargar archivo: " + ex.getMessage());
        }
    }

    public void crearEtiqueta(String nombre, String descripcion, String color){
        if(nombre==null || nombre.trim().isEmpty()){
            JOptionPane.showMessageDialog(vista, "Nombre inválido");
            return;
        }
        if(proyecto.buscarEtiquetaPorNombre(nombre)!=null){
            JOptionPane.showMessageDialog(vista, "La etiqueta ya existe");
            return;
        }
        Etiqueta e = new Etiqueta(nombre, descripcion, color);
        proyecto.agregarEtiqueta(e);
        vista.actualizarCodebook();
    }

    public void editarEtiqueta(String oldNombre, String nuevoNombre, String nuevaDesc, String nuevoColor){
        if (oldNombre == null) return;
        Etiqueta e = proyecto.buscarEtiquetaPorNombre(oldNombre);
        if (e == null) {
            JOptionPane.showMessageDialog(vista, "Etiqueta no encontrada");
            return;
        }
        // Si cambió el nombre, verificar duplicado
        if (!oldNombre.equalsIgnoreCase(nuevoNombre) && proyecto.buscarEtiquetaPorNombre(nuevoNombre) != null) {
            JOptionPane.showMessageDialog(vista, "Ya existe otra etiqueta con ese nombre");
            return;
        }
        e.setNombre(nuevoNombre);
        e.setDescripcion(nuevaDesc);
        e.setColor(nuevoColor);
        // Propagar color a fragmentos asignados
        for (Fragmento f : e.getFragmentos()) {
            if (f != null) f.asignarEtiqueta(e);
        }
        vista.actualizarCodebook();
    }

    public void aplicarEtiquetaAFragmento(String etiqNombre, String textoFragmento){
        Etiqueta e = proyecto.buscarEtiquetaPorNombre(etiqNombre);
        if(e == null){
            JOptionPane.showMessageDialog(vista, "Etiqueta no encontrada");
            return;
        }
        Fragmento f = new Fragmento(textoFragmento, -1, -1);
        // asigna etiqueta (esto setea color en el fragmento)
        e.agregarFragmento(f);
        vista.actualizarCodebook();
    }

    // Nueva funcionalidad: crear hipótesis
    public void crearHipotesis(String texto){
        if(texto == null || texto.trim().isEmpty()){
            JOptionPane.showMessageDialog(vista, "Hipótesis vacía");
            return;
        }
        proyecto.agregarHipotesis(texto.trim());
        vista.actualizarCodebook();
        JOptionPane.showMessageDialog(vista, "Hipótesis agregada");
    }

    public void guardarProyecto(File destino){
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(destino))){
            oos.writeObject(proyecto);
            JOptionPane.showMessageDialog(vista, "Proyecto guardado en: " + destino.getAbsolutePath());
        } catch (IOException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al guardar: " + ex.getMessage());
        }
    }

    public void cargarProyecto(File origen){
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(origen))){
            proyecto = (Proyecto) ois.readObject();
            vista.actualizarListaDocumentos();
            vista.actualizarCodebook();
            JOptionPane.showMessageDialog(vista, "Proyecto cargado: " + proyecto.getNombre());
        } catch (IOException | ClassNotFoundException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al cargar proyecto: " + ex.getMessage());
        }
    }

    // nuevo: eliminar documento por título
    public void eliminarDocumento(String titulo) {
        if (titulo == null || titulo.isEmpty()) return;
        Iterator<Documento> it = proyecto.getDocumentos().iterator();
        while (it.hasNext()) {
            Documento d = it.next();
            if (d != null && titulo.equals(d.getTitulo())) {
                it.remove();
                vista.actualizarListaDocumentos();
                return;
            }
        }
    }

    public List<Etiqueta> obtenerEtiquetas(){
        return proyecto.getEtiquetas();
    }

    public List<Documento> obtenerDocumentos(){
        return proyecto.getDocumentos();
    }
}
