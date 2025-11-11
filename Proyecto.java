import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Proyecto implements Serializable {
    private String nombre;
    private String descripcion;
    private List<Documento> documentos;
    private List<Etiqueta> etiquetas;
    private List<String> hipotesis;

    public Proyecto(String nombre) {
        this.nombre = nombre;
        this.descripcion = "";
        this.documentos = new ArrayList<>();
        this.etiquetas = new ArrayList<>();
        this.hipotesis = new ArrayList<>();
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public List<Documento> getDocumentos() { return documentos; }
    public List<Etiqueta> getEtiquetas() { return etiquetas; }
    public List<String> getHipotesis() { return hipotesis; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public void agregarDocumento(Documento d) {
        if (d != null && !documentos.contains(d)) {
            documentos.add(d);
        }
    }

    public void agregarEtiqueta(Etiqueta e) {
        if (e != null && !etiquetas.contains(e)) {
            etiquetas.add(e);
        }
    }

    public void agregarHipotesis(String hipotesis) {
        if (hipotesis != null && !hipotesis.trim().isEmpty() && !this.hipotesis.contains(hipotesis)) {
            this.hipotesis.add(hipotesis.trim());
        }
    }

    public Documento buscarDocumentoPorTitulo(String titulo) {
        if (titulo == null || documentos == null) return null;
        for (Documento d : documentos) {
            if (d != null && titulo.equals(d.getTitulo())) {
                return d;
            }
        }
        return null;
    }

    public Etiqueta buscarEtiquetaPorNombre(String nombre) {
        if (nombre == null || etiquetas == null) return null;
        for (Etiqueta e : etiquetas) {
            if (e != null && nombre.equals(e.getNombre())) {
                return e;
            }
        }
        return null;
    }
}
