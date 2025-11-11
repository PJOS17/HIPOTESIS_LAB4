import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControladorAnalisis {
    private final Analisis analisis;
    private final Proyecto proyecto;

    public ControladorAnalisis(Proyecto p) {
        if (p == null) {
            throw new IllegalArgumentException("Proyecto no puede ser null");
        }
        this.proyecto = p;
        this.analisis = new Analisis(p);
    }

    public void generarResumen(String etiqueta) {
        if (etiqueta == null || etiqueta.trim().isEmpty()) {
            throw new IllegalArgumentException("Etiqueta no puede ser null o vacía");
        }
        analisis.generarResumenPorEtiqueta(etiqueta.trim());
    }

    public void generarMetricas() {
        analisis.generarMetricas();
    }

    public Map<String, Integer> frecuenciaPalabras() {
        Map<String, Integer> resultado = analisis.frecuenciaPalabras();
        return resultado != null ? resultado : Collections.emptyMap();
    }

    public int contarPalabrasTotal() {
        int total = 0;
        Map<String,Integer> freq = analisis.frecuenciaPalabras();
        for (Integer c : freq.values()) total += c;
        return total;
    }

    public int contarPalabrasTotales() { return contarPalabrasTotal(); }

    public int contarEtiquetas() {
        return proyecto.getEtiquetas() != null ? proyecto.getEtiquetas().size() : 0;
    }

    public int contarCorrecciones() {
        int c = 0;
        if (proyecto.getEtiquetas() == null) return 0;
        for (Etiqueta e : proyecto.getEtiquetas()) {
            if (e == null || e.getFragmentos() == null) continue;
            for (Fragmento f : e.getFragmentos()) {
                if (f != null && f.isCorregido()) c++;
            }
        }
        return c;
    }

    public List<Etiqueta> getEtiquetas() {
        return proyecto.getEtiquetas() != null ?
               proyecto.getEtiquetas() :
               Collections.emptyList();
    }

    public List<String> getHipotesis() {
        return proyecto.getHipotesis();
    }

    private int contarPalabrasEnTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) return 0;
        String[] palabras = texto.split("\\W+");
        int count = 0;
        for (String p : palabras) {
            if (p.trim().length() > 0) count++;
        }
        return count;
    }

    public Map<String, Integer> contarPalabrasEtiqueta() {
        Map<String, Integer> resultado = new HashMap<>();
        if (proyecto.getEtiquetas() == null) return resultado;
        for (Etiqueta e : proyecto.getEtiquetas()) {
            if (e == null || e.getFragmentos() == null) continue;
            int total = 0;
            for (Fragmento f : e.getFragmentos()) {
                if (f != null && f.getTexto() != null) {
                    total += contarPalabrasEnTexto(f.getTexto());
                }
            }
            resultado.put(e.getNombre(), total);
        }
        return resultado;
    }

    public void exportarCodificacionesCSV(String ruta) throws IOException {
        if (ruta == null || ruta.trim().isEmpty()) {
            throw new IllegalArgumentException("Ruta no puede ser null o vacía");
        }

        try (FileWriter fw = new FileWriter(ruta)) {
            fw.write("Etiqueta,Color,Fragmento\n");
            for (Etiqueta e : proyecto.getEtiquetas()) {
                if (e != null && e.getFragmentos() != null) {
                    for (Fragmento f : e.getFragmentos()) {
                        if (f != null && f.getTexto() != null) {
                            fw.write(String.format("\"%s\",\"%s\",\"%s\"%n",
                                e.getNombre().replace("\"", "'"),
                                e.getColor() != null ? e.getColor().replace("\"", "'") : "",
                                f.getTexto().replace("\"", "'")));
                        }
                    }
                }
            }
        }
    }

    public void exportarAnalisisPlano(String ruta, String textoOriginal, Proyecto proyecto, List<String> hipotesis) throws IOException {
        if (ruta == null || ruta.trim().isEmpty()) throw new IllegalArgumentException("Ruta inválida");
        try (FileWriter fw = new FileWriter(ruta)) {
            fw.write(textoOriginal == null ? "" : textoOriginal.trim());
            fw.write(System.lineSeparator());
            fw.write(System.lineSeparator());
            fw.write("----- Hipótesis -----");
            fw.write(System.lineSeparator());
            if (hipotesis == null || hipotesis.isEmpty()) {
                fw.write("(ninguna)");
                fw.write(System.lineSeparator());
            } else {
                for (String h : hipotesis) {
                    fw.write("- " + (h == null ? "" : h));
                    fw.write(System.lineSeparator());
                }
            }
        }
    }

    public void exportarDocumentoModificado(String ruta, String textoModificado) throws IOException {
        if (ruta == null || ruta.trim().isEmpty()) throw new IllegalArgumentException("Ruta inválida");
        try (FileWriter fw = new FileWriter(ruta)) {
            fw.write(textoModificado == null ? "" : textoModificado);
        }
    }

    public void guardarProyecto(String ruta) throws IOException {
        if (ruta == null || ruta.trim().isEmpty()) throw new IllegalArgumentException("Ruta inválida");
        try (java.io.ObjectOutputStream oos = new java.io.ObjectOutputStream(new java.io.FileOutputStream(ruta))) {
            oos.writeObject(proyecto);
        }
    }
}

