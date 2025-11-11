import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Documento implements Serializable {
    private String nombre;
    private String autor;
    private String contenido;
    private List<Fragmento> fragmentos;
    private String sourcePath; // ruta de archivo origen (si fue cargado)

    public Documento(String titulo, String autor, String contenido) {
        this.nombre = titulo;
        this.autor = autor;
        this.contenido = contenido;
        this.fragmentos = new ArrayList<>();
        this.sourcePath = null;
    }

    public String getTitulo() { return nombre; }
    public String getAutor() { return autor; }
    public String getContenido() { return contenido; }
    public List<Fragmento> getFragmentos() { return fragmentos; }

    public void setTitulo(String titulo) { this.nombre = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    public void agregarFragmento(Fragmento f) {
        if (f != null && !fragmentos.contains(f)) {
            fragmentos.add(f);
        }
    }

    // source path getters/setters
    public String getSourcePath() { return sourcePath; }
    public void setSourcePath(String path) { this.sourcePath = path; }
}

