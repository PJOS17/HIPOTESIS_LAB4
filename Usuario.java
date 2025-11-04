

import java.io.Serializable;

public class Usuario implements Serializable {
    private String nombre;
    private String correo;
    private int id;
    private String tipo; // admin, investigador, asistente
    private String contrasena;

    public Usuario(String nombre, String correo, int id, String tipo, String contrasena){
        this.nombre = nombre;
        this.correo = correo;
        this.id = id;
        this.tipo = tipo;
        this.contrasena = contrasena;
    }

    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public int getId() { return id; }
    public String getTipo() { return tipo; }

    public boolean puedeEditar() {
        return !"lectura".equalsIgnoreCase(tipo);
    }
}
