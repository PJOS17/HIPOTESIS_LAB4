import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Analisis implements Analizable, Serializable {
    private Proyecto proyecto;

    public Analisis(Proyecto p){
        this.proyecto = p;
    }

    @Override
    public void generarResumenPorEtiqueta(String etiquetaNombre){
        Etiqueta e = proyecto.buscarEtiquetaPorNombre(etiquetaNombre);
        if(e == null){
            System.out.println("Etiqueta no encontrada: " + etiquetaNombre);
            return;
        }
        System.out.println("Resumen para etiqueta: " + etiquetaNombre);
        for(Fragmento f : e.getFragmentos()){
            System.out.println(" - " + f.getTexto());
        }
    }

    @Override
    public void generarMetricas(){
        System.out.println("Métricas del proyecto: " + proyecto.getNombre());
        Map<String, Integer> m = new HashMap<>();
        for(Etiqueta e : proyecto.getEtiquetas()){
            m.put(e.getNombre(), e.contarFragmentos());
        }
        m.forEach((k,v)-> System.out.println(k + ": " + v + " fragmentos"));
    }

    // Métrica simple: frecuencia de palabras en todo el proyecto
    public Map<String, Integer> frecuenciaPalabras(){
        Map<String,Integer> freq = new HashMap<>();
        if (proyecto == null || proyecto.getDocumentos() == null) {
            return freq;
        }

        for (Documento d : proyecto.getDocumentos()) {
            if (d == null || d.getContenido() == null) continue;

            String[] tokens = d.getContenido().split("\\W+");
            for (String t : tokens) {
                if (t == null || t.trim().isEmpty()) continue;
                String w = t.toLowerCase();
                freq.put(w, freq.getOrDefault(w,0)+1);
            }
        }
        return freq;
    }

    // Genera un resumen final incluyendo hipótesis y conteos básicos
    public void generarResumenFinal() {
        System.out.println("----- Resumen final -----");
        int totalPalabras = 0;
        Map<String,Integer> freq = frecuenciaPalabras();
        for (Integer v : freq.values()) totalPalabras += v;

        int totalEtiquetas = proyecto.getEtiquetas() != null ? proyecto.getEtiquetas().size() : 0;
        int totalCorrecciones = 0;
        if (proyecto.getEtiquetas() != null) {
            for (Etiqueta e : proyecto.getEtiquetas()) {
                if (e == null || e.getFragmentos() == null) continue;
                for (Fragmento f : e.getFragmentos()) {
                    if (f != null && f.isCorregido()) totalCorrecciones++;
                }
            }
        }

        System.out.println("Palabras totales: " + totalPalabras);
        System.out.println("Etiquetas: " + totalEtiquetas);
        System.out.println("Correcciones marcadas: " + totalCorrecciones);

        System.out.println("Hipótesis:");
        if (proyecto.getHipotesis() == null || proyecto.getHipotesis().isEmpty()) {
            System.out.println(" (ninguna)");
        } else {
            for (String h : proyecto.getHipotesis()) {
                System.out.println(" - " + h);
            }
        }
        System.out.println("-------------------------");
    }
}
