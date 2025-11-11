import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Etiqueta implements Serializable {
    private String nombre;
    private String descripcion;
    private String color;
    private List<Fragmento> fragmentos;

    public Etiqueta(String nombre, String descripcion, String color) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.color = color;
        this.fragmentos = new ArrayList<>();
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getColor() { return color; }
    public List<Fragmento> getFragmentos() { return fragmentos; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setColor(String color) { 
        this.color = color;
        // Propagar color a fragmentos
        for (Fragmento f : fragmentos) {
            if (f != null) f.setColor(color);
        }
    }

    public void agregarFragmento(Fragmento f) {
        if (f != null && !fragmentos.contains(f)) {
            fragmentos.add(f);
            f.asignarEtiqueta(this);
        }
    }

    public int contarFragmentos() {
        return fragmentos != null ? fragmentos.size() : 0;
    }
}
