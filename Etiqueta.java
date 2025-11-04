

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Etiqueta implements Serializable {
    private String nombre;
    private String descripcion;
    private String color; // simple string para UI (ej. "red", "orange")
    private List<Fragmento> fragmentos;

    public Etiqueta(String nombre, String descripcion, String color){
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.color = color;
        this.fragmentos = new ArrayList<>();
    }

    public String getNombre(){ return nombre; }
    public String getDescripcion(){ return descripcion; }
    public String getColor(){ return color; }

    public void setDescripcion(String d){ this.descripcion = d; }
    public void agregarFragmento(Fragmento f){ fragmentos.add(f); }
    public List<Fragmento> getFragmentos(){ return fragmentos; }

    public int contarFragmentos(){ return fragmentos.size(); }
}
