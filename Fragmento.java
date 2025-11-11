import java.io.Serializable;

public class Fragmento implements Serializable {
    private String texto;
    private Etiqueta etiqueta;
    private int inicio = -1;
    private int fin = -1;
    private String color;
    private boolean corregido = false;

    // Constructor con texto y etiqueta
    public Fragmento(String texto, Etiqueta etiqueta) {
        this.texto = texto;
        this.etiqueta = etiqueta;
        this.color = etiqueta != null ? etiqueta.getColor() : null;
    }

    // Constructor con texto e índices
    public Fragmento(String texto, int inicio, int fin) {
        this.texto = texto;
        this.inicio = inicio;
        this.fin = fin;
    }

    public String getTexto() { return texto; }
    public Etiqueta getEtiqueta() { return etiqueta; }
    public int getInicio() { return inicio; }
    public int getFin() { return fin; }
    public String getColor() { return color; }
    public boolean isCorregido() { return corregido; }

    public void setTexto(String texto) { this.texto = texto; }
    public void setColor(String color) { this.color = color; }
    public void setCorregido(boolean corregido) { this.corregido = corregido; }

    public void asignarEtiqueta(Etiqueta e) {
        this.etiqueta = e;
        if (e != null) this.color = e.getColor();
    }
}
