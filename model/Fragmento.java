package model;

import java.io.Serializable;

public class Fragmento implements Serializable {
    private String texto;
    private int inicio; // posición inicio en documento (opcional)
    private int fin;    // posición fin (opcional)

    public Fragmento(String texto, int inicio, int fin) {
        this.texto = texto;
        this.inicio = inicio;
        this.fin = fin;
    }

    public String getTexto() { return texto; }

    @Override
    public String toString() {
        return texto;
    }
}
