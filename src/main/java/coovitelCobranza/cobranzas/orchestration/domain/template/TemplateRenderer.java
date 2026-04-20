package coovitelCobranza.cobranzas.orchestration.domain.template;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renderer minimalista de plantillas estilo Mustache.
 *
 * <p>Reemplaza ocurrencias de {@code {{placeholder}}} por los valores del
 * map recibido. Si un placeholder no está en el map, se deja el token tal
 * cual (facilita debugging). Si el valor es {@code null} se trata como cadena
 * vacía.</p>
 *
 * <p>No pretende ser un motor de plantillas completo: sólo sustitución plana
 * porque los mensajes transaccionales de cobranza son cortos y no necesitan
 * ni bucles ni condicionales. Si en el futuro se necesitan lógicas, se puede
 * migrar a Handlebars.java manteniendo este puerto.</p>
 */
@Component
public class TemplateRenderer {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_\\.]+)\\s*}}");

    /**
     * Renderiza la plantilla sustituyendo {@code {{clave}}} por los valores
     * del mapa. Devuelve cadena vacía cuando la plantilla es {@code null}.
     *
     * @param template plantilla en formato String (puede ser null).
     * @param values   valores a sustituir; {@code null} se trata como {@code Map.of()}.
     */
    public String render(String template, Map<String, ?> values) {
        if (template == null || template.isEmpty()) {
            return "";
        }
        Map<String, ?> safeValues = values != null ? values : Map.of();
        Matcher matcher = PLACEHOLDER.matcher(template);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = safeValues.get(key);
            String replacement = value == null ? matcher.group(0) : value.toString();
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
