package coovitelCobranza.cobranzas.interaccion.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 📱 MODELO DE INTERACCIÓN CON CLIENTE (VERSIÓN EN INGLÉS)
 * 
 * Esta clase representa una INTERACCIÓN o COMUNICACIÓN con un cliente.
 * Una interacción es cualquier contacto (SMS, WhatsApp, Email, Llamada) que se realiza
 * con el cliente para cobrarle.
 * 
 * RESPONSABILIDADES:
 * - Register que se intentó contactar a un cliente
 * - Almacenar el canal usado (SMS, WhatsApp, Email, Llamada)
 * - Almacenar la plantilla de mensaje usada
 * - Register el resultado (entregado, leído, fallido, etc.)
 * 
 * CASOS DE USO:
 *   1. Send SMS de recordatorio de pago
 *   2. Send email con details de deuda
 *   3. Send WhatsApp con aviso
 *   4. Register llamada telefónica
 * 
 * EJEMPLO DE USO:
 *   // Create una interacción (enviar SMS)
 *   Interaction interaction = Interaction.create(
 *       caseId,                      // ID del caso
 *       Channel.SMS,                 // Send por SMS
 *       "template_pago_pendiente"    // Plantilla a usar
 *   );
 *   
 *   // Register que se entregó exitosamente
 *   interaction.markDelivered();
 *   
 *   // O si el cliente leyó el mensaje
 *   interaction.markRead();
 *   
 *   // O si falló el envío
 *   interaction.markFailed();
 * 
 * ENUM Channel (Channel de Comunicación):
 *   - SMS: Mensaje de texto
 *   - WHATSAPP: Mensaje por WhatsApp
 *   - EMAIL: Correo electrónico
 *   - VOICE: Llamada telefónica
 * 
 * ENUM ResultStatus (Result de la Interacción):
 *   - PENDING: Esperando envío
 *   - DELIVERED: Entregado al cliente
 *   - READ: El cliente leyó el mensaje
 *   - ANSWERED: El cliente respondió
 *   - FAILED: Falló el envío
 *   - NO_CONTACT: No se pudo contactar (teléfono inactivo, etc)
 */
public class Interaction {

    private final Long id;
    private final Long caseId;
    private final Channel channel;
    private final String template;
    private ResultStatus resultStatus;
    private final LocalDateTime createdAt;

    /**
     * Private constructor for controlled object creation.
     */
    private Interaction(Long id, Long caseId, Channel channel, String template, ResultStatus resultStatus,
                        LocalDateTime createdAt) {
        this.id = id;
        this.caseId = Objects.requireNonNull(caseId, "caseId is required");
        this.channel = Objects.requireNonNull(channel, "channel is required");
        this.template = template;
        this.resultStatus = Objects.requireNonNull(resultStatus, "resultStatus is required");
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    /**
     * Factory method to create a new interaction.
     *
     * @param caseId    the case ID this interaction belongs to
     * @param channel   the communication channel (SMS, Email, WhatsApp, etc.)
     * @param template  the template used for this interaction
     * @return a new Interaction aggregate
     */
    public static Interaction create(Long caseId, Channel channel, String template) {
        return new Interaction(null, caseId, channel, template, ResultStatus.PENDING, LocalDateTime.now());
    }

    public static Interaction crear(Long caseId, Channel channel, String template) {
        return create(caseId, channel, template);
    }

    /**
     * Factory method to reconstruct an interaction from persistence.
     *
     * @param id           the interaction ID
     * @param caseId       the case ID
     * @param channel      the channel
     * @param template     the template
     * @param resultStatus the result status
     * @param createdAt    the creation datetime
     * @return the reconstructed Interaction
     */
    public static Interaction reconstruct(Long id, Long caseId, Channel channel, String template,
                                          ResultStatus resultStatus, LocalDateTime createdAt) {
        return new Interaction(id, caseId, channel, template, resultStatus, createdAt);
    }

    /**
     * Mark this interaction as delivered.
     */
    public void markDelivered() {
        this.resultStatus = ResultStatus.DELIVERED;
    }

    public void marcarEntregada() {
        markDelivered();
    }

    /**
     * Mark this interaction as read.
     */
    public void markRead() {
        this.resultStatus = ResultStatus.READ;
    }

    public void marcarLeida() {
        markRead();
    }

    /**
     * Mark this interaction as failed.
     */
    public void markFailed() {
        this.resultStatus = ResultStatus.FAILED;
    }

    public void marcarFallida() {
        markFailed();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getCaseId() {
        return caseId;
    }

    public Channel getChannel() {
        return channel;
    }

    public String getTemplate() {
        return template;
    }

    public ResultStatus getResultStatusStatus() {
        return resultStatus;
    }

    public ResultStatus getResultStatus() {
        return resultStatus;
    }

    public ResultStatus getResult() {
        return resultStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Communication channels supported by the system.
     */
    public enum Channel {
        SMS,       // Text message
        WHATSAPP,  // WhatsApp messaging
        EMAIL,     // Email
        VOICE      // Voice call
    }

    /**
     * Result status of an interaction throughout its lifecycle.
     */
    public enum ResultStatus {
        PENDING,      // Awaiting delivery
        DELIVERED,    // Successfully delivered
        READ,         // Customer has read the message
        ANSWERED,     // Customer has responded
        FAILED,       // Delivery failed
        NO_CONTACT    // Unable to contact customer
    }
}
