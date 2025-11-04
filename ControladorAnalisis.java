

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
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

    public void exportarCodificacionesCSV(String ruta) throws IOException {
        if (ruta == null || ruta.trim().isEmpty()) {
            throw new IllegalArgumentException("Ruta no puede ser null o vacía");
        }

        try (FileWriter fw = new FileWriter(ruta)) {
            fw.write("Etiqueta,Fragmento\n");
            for (Etiqueta e : proyecto.getEtiquetas()) {
                if (e != null && e.getFragmentos() != null) {
                    for (Fragmento f : e.getFragmentos()) {
                        if (f != null && f.getTexto() != null) {
                            fw.write(String.format("\"%s\",\"%s\"%n",
                                e.getNombre().replace("\"", "'"),
                                f.getTexto().replace("\"", "'")));
                        }
                    }
                }
            }
        }
    }

    public List<Etiqueta> getEtiquetas() {
        return proyecto.getEtiquetas() != null ? 
               proyecto.getEtiquetas() : 
               Collections.emptyList();
    }
}

