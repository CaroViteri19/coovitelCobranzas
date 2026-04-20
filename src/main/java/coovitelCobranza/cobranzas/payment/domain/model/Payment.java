package coovitelCobranza.cobranzas.payment.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 💰 MODELO DE PAGO (VERSIÓN EN INGLÉS)
 * 
 * Esta clase representa un PAGO en el sistema de cobranzas.
 * Un pago es una transacción de dinero que un cliente realiza para pagar su obligación.
 * 
 * RESPONSABILIDADES:
 * - Almacenar información del pago (ID, monto, referencia externa, método)
 * - Mantener el estado del pago (pendiente, confirmado, rechazado, expirado)
 * - Permitir confirm un pago cuando se valida
 * - Permitir reject un pago si hay error
 * 
 * FLUJO DE ESTADO:
 *   1. PENDING (Pendiente): Se crea cuando se inicia el pago
 *   2. CONFIRMED (Confirmed): Se confirma cuando recibimos validación del banco
 *   3. REJECTED (Rechazado): Si falla la validación
 *   4. EXPIRED (Expirado): Si pasa mucho tiempo sin confirmse
 * 
 * EJEMPLO DE USO:
 *   // Create un nuevo pago
 *   Payment pago = Payment.createPending(
 *       obligationId, 
 *       BigDecimal.valueOf(500000),  // Monto
 *       "REF-12345",                  // Referencia del banco
 *       PaymentMethod.PSE             // Método de pago
 *   );
 *   
 *   // Cuando llega confirmación del banco
 *   pago.confirm();
 *   
 *   // O si hay error
 *   pago.reject();
 * 
 * ENUM PaymentMethod (Método de Payment):
 *   - PSE: Payment Seguro en Línea (transferencia bancaria Colombia)
 *   - CARD: Tarjeta de crédito/débito
 *   - TRANSFER: Transferencia manual
 *   - OFFICE: Payment en oficina física
 * 
 * ENUM PaymentStatus (Status del Payment):
 *   - PENDING: Esperando confirmación
 *   - CONFIRMED: Confirmed y validado
 *   - REJECTED: Rechazado por error
 *   - EXPIRED: Expiró el plazo
 */
public class Payment {

    private final Long id;
    private final Long obligationId;
    private final BigDecimal amount;
    private final String externalReference;
    private final PaymentMethod method;
    private PaymentStatus status;
    private LocalDateTime confirmedAt;
    private final LocalDateTime createdAt;

    private Payment(Long id,
                    Long obligationId,
                    BigDecimal amount,
                    String externalReference,
                    PaymentMethod method,
                    PaymentStatus status,
                    LocalDateTime confirmedAt,
                    LocalDateTime createdAt) {
        this.id = id;
        this.obligationId = Objects.requireNonNull(obligationId, "obligationId is required");
        this.amount = Objects.requireNonNull(amount, "amount is required");
        this.externalReference = Objects.requireNonNull(externalReference, "externalReference is required");
        this.method = Objects.requireNonNull(method, "method is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.confirmedAt = confirmedAt;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    /**
     * Factory method to create a new pending payment.
     *
     * @param obligationId       the obligation ID
     * @param amount             the payment amount
     * @param externalReference  the external reference (from payment gateway)
     * @param method             the payment method used
     * @return a new Payment aggregate in PENDING status
     */
    public static Payment createPending(Long obligationId, BigDecimal amount, String externalReference, PaymentMethod method) {
        return new Payment(null, obligationId, amount, externalReference, method, PaymentStatus.PENDING, null,
                LocalDateTime.now());
    }

    public static Payment crearPendiente(Long obligationId, BigDecimal amount, String externalReference, PaymentMethod method) {
        return createPending(obligationId, amount, externalReference, method);
    }

    /**
     * Factory method to reconstruct a payment from persistence.
     *
     * @param id                 the payment ID
     * @param obligationId       the obligation ID
     * @param amount             the amount
     * @param externalReference  the external reference
     * @param method             the payment method
     * @param status             the payment status
     * @param confirmedAt        the confirmation datetime
     * @param createdAt          the creation datetime
     * @return the reconstructed Payment
     */
    public static Payment reconstruct(Long id,
                                      Long obligationId,
                                      BigDecimal amount,
                                      String externalReference,
                                      PaymentMethod method,
                                      PaymentStatus status,
                                      LocalDateTime confirmedAt,
                                      LocalDateTime createdAt) {
        return new Payment(id, obligationId, amount, externalReference, method, status, confirmedAt, createdAt);
    }

    /**
     * Confirm this payment and set the confirmation timestamp.
     */
    public void confirm() {
        this.status = PaymentStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
    }

    public void confirmar() {
        confirm();
    }

    /**
     * Reject this payment.
     */
    public void reject() {
        this.status = PaymentStatus.REJECTED;
    }

    public void rechazar() {
        reject();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getObligationId() {
        return obligationId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getValor() {
        return getAmount();
    }

    public String getExternalReference() {
        return externalReference;
    }

    public String getReferenciaExterna() {
        return getExternalReference();
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentMethod getMetodo() {
        return getMethod();
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public PaymentStatus getEstado() {
        return getStatus();
    }

    public LocalDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Payment methods supported by the system.
     */
    public enum PaymentMethod {
        PSE,              // Colombian bank transfer
        CARD,             // Credit/Debit card
        TRANSFER,         // Bank transfer
        OFFICE            // Payment at office
    }

    /**
     * Payment status throughout its lifecycle.
     */
    public enum PaymentStatus {
        PENDING,          // Awaiting confirmation
        CONFIRMED,        // Successfully confirmed
        REJECTED,         // Rejected by system or user
        EXPIRED           // Payment link expired
    }
}
