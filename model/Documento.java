package model;

import java.io.Serializable;

public abstract class Documento implements Serializable {
    protected String titulo;
    protected String autor;
    protected String contenido;

    public Documento(String titulo, String autor, String contenido){
        this.titulo = titulo;
        this.autor = autor;
        this.contenido = contenido;
    }

    public String getTitulo(){ return titulo; }
    public String getAutor(){ return autor; }
    public String getContenido(){ return contenido; }
    public void setContenido(String c){ this.contenido = c; }
}
