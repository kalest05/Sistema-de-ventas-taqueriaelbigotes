package util;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

/*
 * Utilidad para cargar imágenes desde el classpath.
 * Verifica y corrige el formato de la ruta automáticamente.
 */
public class ImagenUtil {

    /*
     * Carga una imagen desde el classpath y la escala al tamaño indicado.
     * Verifica automáticamente:
     *   - Quita "src/" si la ruta empieza con eso
     *   - Agrega "/" al inicio si no lo tiene
     *   - Si no se encuentra con "/", intenta sin "/"
     * Retorna null si no se puede cargar (sin imprimir errores).
     */
    public static Image cargar(String urlFoto, int ancho, int alto) {
        if (urlFoto == null || urlFoto.trim().isEmpty()) return null;

        String ruta = urlFoto.trim();

        // Quitar "src/" si viene así desde la BD
        if (ruta.startsWith("src/")) ruta = ruta.substring(4);

        // Asegurar que empiece con "/"
        String rutaConBarra = ruta.startsWith("/") ? ruta : "/" + ruta;

        // Intento 1: classpath con barra (más confiable en Eclipse)
        URL url = ImagenUtil.class.getResource(rutaConBarra);

        // Intento 2: classloader sin barra
        if (url == null) {
            String rutaSinBarra = rutaConBarra.substring(1);
            url = ImagenUtil.class.getClassLoader().getResource(rutaSinBarra);
        }

        // Intento 3: desde disco relativo al directorio de trabajo
        // Útil cuando la imagen se acaba de copiar y Eclipse no la compiló aún
        if (url == null) {
            String rutaSinBarra = rutaConBarra.substring(1);
            java.io.File archivo = new java.io.File("src/" + rutaSinBarra);
            if (!archivo.exists()) archivo = new java.io.File(rutaSinBarra);
            if (archivo.exists()) {
                try { url = archivo.toURI().toURL(); } catch (Exception ignored) {}
            }
        }

        if (url == null) return null;

        return new ImageIcon(url).getImage()
            .getScaledInstance(ancho, alto, Image.SCALE_SMOOTH);
    }

    /*
     * Convierte un nombre de producto a nombre de archivo de imagen.
     * Ejemplo: "Taco de Carne" → "tacodecarne.jpg"
     * Protocolo: minúsculas, sin espacios, sin caracteres especiales, extensión .jpg
     */
    public static String generarNombreArchivo(String nombreProducto) {
        if (nombreProducto == null || nombreProducto.trim().isEmpty()) return "producto.jpg";

        String nombre = nombreProducto.trim().toLowerCase();

        // Reemplazar caracteres con acento
        nombre = nombre.replace("á", "a").replace("é", "e").replace("í", "i")
                       .replace("ó", "o").replace("ú", "u").replace("ü", "u")
                       .replace("ñ", "n");

        // Quitar todo lo que no sea letra o número
        nombre = nombre.replaceAll("[^a-z0-9]", "");

        return nombre + ".jpg";
    }

    /*
     * Genera la ruta completa para guardar en BD.
     * Ejemplo: "Taco de Carne" → "/imagenes/tacodecarne.jpg"
     */
    public static String generarRutaBD(String nombreProducto) {
        return "/imagenes/" + generarNombreArchivo(nombreProducto);
    }
}
