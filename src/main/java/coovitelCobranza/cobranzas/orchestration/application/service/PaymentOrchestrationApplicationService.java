package coovitelCobranza.cobranzas.orchestration.application.service;

import coovitelCobranza.cobranzas.casemanagement.domain.model.Case;
import coovitelCobranza.cobranzas.casemanagement.domain.repository.CaseRepository;
import coovitelCobranza.cobranzas.client.domain.model.Client;
import coovitelCobranza.cobranzas.client.domain.repository.ClientRepository;
import coovitelCobranza.cobranzas.interaction.domain.model.Interaction;
import coovitelCobranza.cobranzas.interaction.domain.repository.InteractionRepository;
import coovitelCobranza.cobranzas.obligation.domain.model.Obligation;
import coovitelCobranza.cobranzas.obligation.domain.repository.ObligationRepository;
import coovitelCobranza.cobranzas.orchestration.application.dto.ChannelDispatchResult;
import coovitelCobranza.cobranzas.orchestration.application.dto.SendPaymentLinkRequest;
import coovitelCobranza.cobranzas.orchestration.application.dto.SendPaymentLinkResponse;
import coovitelCobranza.cobranzas.orchestration.application.exception.CaseForOrchestrationNotFoundException;
import coovitelCobranza.cobranzas.orchestration.application.exception.ClientForOrchestrationNotFoundException;
import coovitelCobranza.cobranzas.orchestration.domain.service.ChannelOrchestrator;
import coovitelCobranza.cobranzas.orchestration.domain.template.PaymentLinkTemplates;
import coovitelCobranza.cobranzas.orchestration.domain.template.TemplateRenderer;
import coovitelCobranza.cobranzas.payment.application.dto.GenerateLinkRequest;
import coovitelCobranza.cobranzas.payment.application.dto.GenerateLinkResponse;
import coovitelCobranza.cobranzas.payment.application.exception.ObligationForPaymentNotFoundException;
import coovitelCobranza.cobranzas.payment.application.service.PaymentApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Caso de uso compuesto que:
 *
 * <ol>
 *   <li>Toma un caso de gestión y obtiene su obligación + cliente.</li>
 *   <li>Pide a la pasarela un link de pago vía {@link PaymentApplicationService}.
 *       Eso persiste además un {@code Payment} en estado {@code PENDING}.</li>
 *   <li>Renderiza las plantillas del catálogo {@link PaymentLinkTemplates}
 *       usando {@link TemplateRenderer}.</li>
 *   <li>Envía el link por los canales consentidos del cliente (o los canales
 *       explícitos del request) a través de {@link ChannelOrchestrator}.</li>
 *   <li>Persiste cada {@link Interaction} resultante.</li>
 * </ol>
 *
 * <p>Best-effort multicanal: si un canal falla no se aborta el resto. La
 * transacción se abre para mantener consistencia del {@code Payment} y las
 * {@code Interaction}s pero no se revierte por fallo de proveedor externo
 * (los senders ya reportan éxito/fallo sin lanzar excepción).</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentOrchestrationApplicationService {

    private static final DateTimeFormatter EXPIRATION_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final CaseRepository caseRepository;
    private final ObligationRepository obligationRepository;
    private final ClientRepository clientRepository;
    private final InteractionRepository interactionRepository;
    private final PaymentApplicationService paymentApplicationService;
    private final ChannelOrchestrator channelOrchestrator;
    private final TemplateRenderer templateRenderer;

    /**
     * Ejecuta el flujo completo: genera link + envía por los canales
     * correspondientes.
     *
     * @param caseId id del caso (obligatorio).
     * @param request opciones (puede ser {@link SendPaymentLinkRequest#defaults()}).
     * @return resumen con el {@code paymentId}, la URL y los resultados por canal.
     */
    @Transactional
    public SendPaymentLinkResponse generateAndSendLinkForCase(Long caseId, SendPaymentLinkRequest request) {
        SendPaymentLinkRequest safeRequest = request != null ? request : SendPaymentLinkRequest.defaults();

        // 1) Cargar caso → obligación → cliente.
        Case casoGestion = caseRepository.findById(caseId)
                .orElseThrow(() -> new CaseForOrchestrationNotFoundException(caseId));

        Obligation obligation = obligationRepository.findById(casoGestion.getObligationId())
                .orElseThrow(() -> new ObligationForPaymentNotFoundException(casoGestion.getObligationId()));

        Client client = clientRepository.findById(obligation.getCustomerId())
                .orElseThrow(() -> new ClientForOrchestrationNotFoundException(obligation.getCustomerId()));

        // 2) Generar link (persiste Payment PENDING dentro de su propia @Transactional,
        //    que al estar anidada en REQUIRED reusa la nuestra).
        GenerateLinkResponse link = paymentApplicationService.generateLink(
                new GenerateLinkRequest(obligation.getId(), safeRequest.methodOrDefault())
        );

        // 3) Preparar valores para las plantillas.
        Map<String, Object> values = buildTemplateValues(client, obligation, link);

        // 4) Determinar canales a usar.
        Set<Interaction.Channel> targetChannels = resolveChannels(client, safeRequest);

        List<ChannelDispatchResult> dispatches = new ArrayList<>();
        int delivered = 0;
        int attempted = 0;

        // 5) Disparar por canal, renderizando y persistiendo cada Interaction.
        for (Interaction.Channel channel : targetChannels) {
            String destination = destinationFor(channel, client);
            if (destination == null || destination.isBlank()) {
                log.warn("Skip canal {}: destino vacío para cliente {}", channel, client.getId());
                dispatches.add(ChannelDispatchResult.skipped(channel, "destino no disponible"));
                continue;
            }

            String body = templateRenderer.render(PaymentLinkTemplates.bodyFor(channel), values);
            attempted++;

            Interaction interaction = channelOrchestrator.send(caseId, channel, body, destination);
            Interaction persisted = interactionRepository.save(interaction);

            if (persisted.getResultStatus() == Interaction.ResultStatus.DELIVERED) {
                delivered++;
                dispatches.add(ChannelDispatchResult.delivered(channel, destination, persisted.getId()));
            } else {
                dispatches.add(ChannelDispatchResult.failed(
                        channel, destination, persisted.getId(),
                        "proveedor reportó " + persisted.getResultStatus()));
            }
        }

        log.info("Link de pago enviado: caseId={} paymentId={} canalesIntentados={} canalesEntregados={}",
                caseId, link.paymentId(), attempted, delivered);

        return new SendPaymentLinkResponse(
                caseId,
                obligation.getId(),
                link.paymentId(),
                link.paymentUrl(),
                attempted,
                delivered,
                List.copyOf(dispatches)
        );
    }

    // ---------------------------------------------------------------------
    // helpers
    // ---------------------------------------------------------------------

    private Map<String, Object> buildTemplateValues(Client client,
                                                    Obligation obligation,
                                                    GenerateLinkResponse link) {
        NumberFormat amountFormat = NumberFormat.getNumberInstance(new Locale("es", "CO"));
        amountFormat.setMaximumFractionDigits(0);
        String amountStr = amountFormat.format(obligation.getTotalBalance());

        LocalDateTime expiration = link.expirationDate();
        String expirationStr = expiration != null ? EXPIRATION_FORMAT.format(expiration) : "";

        return Map.of(
                "fullName", client.getFullName(),
                "obligationNumber", obligation.getObligationNumber(),
                "amount", amountStr,
                "url", link.paymentUrl(),
                "expirationDate", expirationStr
        );
    }

    private Set<Interaction.Channel> resolveChannels(Client client, SendPaymentLinkRequest request) {
        if (request.hasExplicitChannels()) {
            // VOICE no tiene plantilla de link → se filtra defensivamente.
            Set<Interaction.Channel> explicit = EnumSet.copyOf(request.channels());
            explicit.remove(Interaction.Channel.VOICE);
            return explicit;
        }
        Set<Interaction.Channel> consented = EnumSet.noneOf(Interaction.Channel.class);
        if (client.isAceptaSms())      consented.add(Interaction.Channel.SMS);
        if (client.isAceptaEmail())    consented.add(Interaction.Channel.EMAIL);
        if (client.isAceptaWhatsApp()) consented.add(Interaction.Channel.WHATSAPP);
        return consented;
    }

    private String destinationFor(Interaction.Channel channel, Client client) {
        return switch (channel) {
            case SMS, WHATSAPP -> client.getTelefono();
            case EMAIL         -> client.getEmail();
            case VOICE         -> null; // no aplica para link de pago
        };
    }
}
