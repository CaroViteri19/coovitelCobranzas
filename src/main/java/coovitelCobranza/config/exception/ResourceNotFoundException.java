package coovitelCobranza.config.exception;

/**
 * Excepción para recursos no encontrados.
 * Se lanza cuando se intenta acceder a un recurso que no existe en la base de datos.
 * Retorna HTTP 404 (Not Found).
 */
public class ResourceNotFoundException extends RuntimeException {

    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;

    /**
     * Constructor con información detallada del recurso no encontrado.
     *
     * @param resourceName nombre del tipo de recurso (ej: "Usuario", "Cuenta")
     * @param fieldName nombre del campo por el cual se buscaba (ej: "id", "email")
     * @param fieldValue valor del campo que no fue encontrado
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrado con %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Constructor con mensaje personalizado.
     *
     * @param message mensaje descriptivo del error
     */
    public ResourceNotFoundException(String message) {
        super(message);
        this.resourceName = null;
        this.fieldName = null;
        this.fieldValue = null;
    }

    /**
     * Obtiene el nombre del recurso que no fue encontrado.
     *
     * @return nombre del recurso o null si se usó constructor con mensaje personalizado
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Obtiene el nombre del campo por el cual se realizó la búsqueda.
     *
     * @return nombre del campo o null si se usó constructor con mensaje personalizado
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Obtiene el valor del campo que no fue encontrado.
     *
     * @return valor del campo o null si se usó constructor con mensaje personalizado
     */
    public Object getFieldValue() {
        return fieldValue;
    }
}
