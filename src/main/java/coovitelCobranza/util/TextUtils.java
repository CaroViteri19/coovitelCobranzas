package coovitelCobranza.util;

import java.text.Normalizer;

/**
 * Utilidades de normalización de texto para datos importados.
 *
 * <p>Garantiza que los campos de texto almacenados en la BD no contengan
 * caracteres con tilde, diéresis u otros diacríticos que puedan causar
 * problemas de encoding en sistemas legacy o comparaciones de cadenas.
 */
public class TextUtils {

    private TextUtils() {
        // Utilidad estática — no instanciar
    }

    /**
     * Normaliza un texto eliminando tildes y caracteres no ASCII.
     *
     * <p>Proceso:
     * <ol>
     *   <li>Descompone los caracteres Unicode combinados (NFD): á → a + ́</li>
     *   <li>Elimina las marcas de combinación diacrítica (tildes, diéresis, etc.)</li>
     *   <li>Elimina cualquier carácter no ASCII restante</li>
     * </ol>
     *
     * <p>Ejemplos:
     * <ul>
     *   <li>{@code "María"} → {@code "Maria"}</li>
     *   <li>{@code "Bogotá"} → {@code "Bogota"}</li>
     *   <li>{@code "Ñoño"} → {@code "Nono"}</li>
     *   <li>{@code null} → {@code null}</li>
     * </ul>
     *
     * @param input texto original (puede ser null)
     * @return texto normalizado sin diacríticos, o null si la entrada es null
     */
    public static String limpiarTexto(String input) {
        if (input == null) return null;

        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        return normalized
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^\\p{ASCII}]", "");
    }
}
