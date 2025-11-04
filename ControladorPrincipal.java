
import javax.swing.*;
import java.io.*;
import java.util.List;

public class ControladorPrincipal {
    private Proyecto proyecto;
    private VistaPrincipal vista;

    public ControladorPrincipal(VistaPrincipal vista){
        this.vista = vista;
        this.proyecto = new Proyecto("Proyecto nuevo");
    }

    public Proyecto getProyecto(){ return proyecto; }

    public void setNombreProyecto(String nombre){
        proyecto = new Proyecto(nombre);
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
            proyecto.agregarDocumento(d);
            vista.actualizarListaDocumentos();
            JOptionPane.showMessageDialog(vista, "Documento cargado: " + file.getName());
        } catch (IOException ex){
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vista, "Error al cargar archivo: " + ex.getMessage());
        }
    }

    public void crearEtiqueta(String nombre, String descripcion, String color){
        if(proyecto.buscarEtiquetaPorNombre(nombre)!=null){
            JOptionPane.showMessageDialog(vista, "La etiqueta ya existe");
            return;
        }
        Etiqueta e = new Etiqueta(nombre, descripcion, color);
        proyecto.agregarEtiqueta(e);
        vista.actualizarCodebook();
    }

    public void aplicarEtiquetaAFragmento(String etiqNombre, String textoFragmento){
        Etiqueta e = proyecto.buscarEtiquetaPorNombre(etiqNombre);
        if(e == null){
            JOptionPane.showMessageDialog(vista, "Etiqueta no encontrada");
            return;
        }
        Fragmento f = new Fragmento(textoFragmento, -1, -1);
        e.agregarFragmento(f);
        vista.actualizarCodebook();
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

    public List<Etiqueta> obtenerEtiquetas(){
        return proyecto.getEtiquetas();
    }

    public List<Documento> obtenerDocumentos(){
        return proyecto.getDocumentos();
    }
}
