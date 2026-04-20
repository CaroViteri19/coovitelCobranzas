package coovitelCobranza.cobranzas.policies.domain.service;

public interface PolicyStrategyService {

    String defineNextAction(Long obligationId, Long customerId, int delinquencyDays, String suggestedChannel);

    boolean allowsContact(String channel, int hourOfDay, int previousAttempts);

    // Backward-compatible wrappers during migration to English API.
    /**
     * @deprecated Use {@link #defineNextAction(Long, Long, int, String)}.
     */
    @Deprecated
    default String definirSiguienteAction(Long obligacionId, Long clienteId, int diasDelinquency, String canalSugerido) {
        return defineNextAction(obligacionId, clienteId, diasDelinquency, canalSugerido);
    }

    /**
     * @deprecated Use {@link #allowsContact(String, int, int)}.
     */
    @Deprecated
    default boolean permiteContact(String canal, int horaDia, int intentosPrevios) {
        return allowsContact(canal, horaDia, intentosPrevios);
    }
}

