

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Proyecto implements Serializable {
    private String nombre;
    private String descripcion;
    private List<Documento> documentos;
    private List<Etiqueta> etiquetas;

    public Proyecto(String nombre){
        this.nombre = nombre;
        this.descripcion = "";
        this.documentos = new ArrayList<>();
        this.etiquetas = new ArrayList<>();
    }

    public String getNombre(){ return nombre; }
    public List<Documento> getDocumentos(){ return documentos; }
    public List<Etiqueta> getEtiquetas(){ return etiquetas; }

    public void agregarDocumento(Documento d){ documentos.add(d); }
    public void agregarEtiqueta(Etiqueta e){ etiquetas.add(e); }

    public Etiqueta buscarEtiquetaPorNombre(String nombre){
        for(Etiqueta e : etiquetas){
            if(e.getNombre().equalsIgnoreCase(nombre)) return e;
        }
        return null;
    }
}
