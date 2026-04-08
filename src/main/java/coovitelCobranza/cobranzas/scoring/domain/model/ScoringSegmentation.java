package coovitelCobranza.cobranzas.scoring.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 📊 MODELO DE SCORING Y SEGMENTACIÓN (VERSIÓN EN INGLÉS)
 * 
 * Esta clase representa el resultado de un ANÁLISIS DE CRÉDITO y SEGMENTACIÓN de cliente.
 * Se usa para clasificar a los clientes en grupos según su riesgo y probabilidad de pago.
 * 
 * RESPONSABILIDADES:
 * - Almacenar el score de crédito (puntuación 0-100)
 * - Almacenar en qué SEGMENTO cae el cliente (bajo riesgo, medio, alto, etc)
 * - Register la versión del modelo de IA usado para calcular el score
 * - Register la razón principal del segmento asignado
 * 
 * CASOS DE USO:
 * 1. CLASIFICACIÓN DE CLIENTES:
 *    - Score 90-100: Clients premium, bajo riesgo → Contact suave
 *    - Score 60-89: Clients normales → Contact estándar
 *    - Score 30-59: Clients en riesgo → Contact más frecuente
 *    - Score 0-29: Clients en alto riesgo → Actiones urgentes
 * 
 * 2. ASIGNACIÓN DE ESTRATEGIA DE COBRANZA:
 *    - Según el score, se asigna qué asesor, qué frecuencia de contacto, etc.
 * 
 * EJEMPLO DE USO:
 *   // Calculate scoring para un cliente
 *   ScoringSegmentation scoring = ScoringSegmentation.create(
 *       customerId,                    // ID del cliente
 *       obligationId,                  // ID de la obligación
 *       78.5,                          // Score calculado por IA
 *       "MEDIUM_RISK",                 // Segmento asignado
 *       "v2.1",                        // Versión del modelo
 *       "Deuda de alto monto"          // Razón del segmento
 *   );
 *   
 *   // Usar el scoring para asignar estrategia
 *   if (scoring.getScore() > 80) {
 *       // Client bajo riesgo - contacto simple
 *   } else if (scoring.getScore() > 50) {
 *       // Client medio riesgo - contacto normal
 *   } else {
 *       // Client alto riesgo - contacto urgente
 *   }
 * 
 * CAMPOS IMPORTANTES:
 *   - score: Puntuación de 0 a 100 (más alto = menos riesgo)
 *   - segment: Clasificación del cliente (BAJO, MEDIO, ALTO)
 *   - modelVersion: Versión del algoritmo de IA usado
 *   - mainReason: Por qué el cliente cayó en ese segmento
 * 
 * NOTAS:
 * - Este es un VALUE OBJECT (inmutable): una vez creado, no cambia
 * - Se crea una vez y se guarda como histórico
 * - Si hay que recalcular, se crea uno nuevo
 */
public class ScoringSegmentation {

    private final Long id;
    private final Long customerId;
    private final Long obligationId;
    private final double score;
    private final String segment;
    private final String modelVersion;
    private final String mainReason;
    private final LocalDateTime createdAt;

    /**
     * Private constructor for controlled object creation.
     */
    private ScoringSegmentation(Long id,
                                Long customerId,
                                Long obligationId,
                                double score,
                                String segment,
                                String modelVersion,
                                String mainReason,
                                LocalDateTime createdAt) {
        this.id = id;
        this.customerId = Objects.requireNonNull(customerId, "customerId is required");
        this.obligationId = Objects.requireNonNull(obligationId, "obligationId is required");
        this.score = score;
        this.segment = Objects.requireNonNull(segment, "segment is required");
        this.modelVersion = Objects.requireNonNull(modelVersion, "modelVersion is required");
        this.mainReason = Objects.requireNonNull(mainReason, "mainReason is required");
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    /**
     * Factory method to create a new scoring segmentation result.
     *
     * @param customerId     the customer ID
     * @param obligationId   the obligation ID
     * @param score          the credit score (0-100)
     * @param segment        the customer segment
     * @param modelVersion   the version of the scoring model used
     * @param mainReason     the primary reason for this segmentation
     * @return a new ScoringSegmentation aggregate
     */
    public static ScoringSegmentation create(Long customerId,
                                             Long obligationId,
                                             double score,
                                             String segment,
                                             String modelVersion,
                                             String mainReason) {
        return new ScoringSegmentation(null, customerId, obligationId, score, segment, modelVersion,
                mainReason, LocalDateTime.now());
    }

    public static ScoringSegmentation crear(Long customerId,
                                            Long obligationId,
                                            double score,
                                            String segment,
                                            String modelVersion,
                                            String mainReason) {
        return create(customerId, obligationId, score, segment, modelVersion, mainReason);
    }

    /**
     * Factory method to reconstruct a scoring from persistence.
     *
     * @param id           the scoring ID
     * @param customerId   the customer ID
     * @param obligationId the obligation ID
     * @param score        the credit score
     * @param segment      the customer segment
     * @param modelVersion the model version
     * @param mainReason   the main reason
     * @param createdAt    the creation datetime
     * @return the reconstructed ScoringSegmentation
     */
    public static ScoringSegmentation reconstruct(Long id,
                                                  Long customerId,
                                                  Long obligationId,
                                                  double score,
                                                  String segment,
                                                  String modelVersion,
                                                  String mainReason,
                                                  LocalDateTime createdAt) {
        return new ScoringSegmentation(id, customerId, obligationId, score, segment, modelVersion,
                mainReason, createdAt);
    }

    public static ScoringSegmentation reconstruir(Long id,
                                                   Long customerId,
                                                   Long obligationId,
                                                   double score,
                                                   String segment,
                                                   String modelVersion,
                                                   String mainReason,
                                                   LocalDateTime createdAt) {
        return reconstruct(id, customerId, obligationId, score, segment, modelVersion, mainReason, createdAt);
    }

    // Getters (immutable value object)
    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public Long getClientId() {
        return getCustomerId();
    }

    public Long getObligationId() {
        return obligationId;
    }

    public double getScore() {
        return score;
    }

    public String getSegment() {
        return segment;
    }

    public String getSegmento() {
        return getSegment();
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public String getVersionModelo() {
        return getModelVersion();
    }

    public String getMainReason() {
        return mainReason;
    }

    public String getRazonPrincipal() {
        return getMainReason();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
