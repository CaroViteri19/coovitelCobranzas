package coovitelCobranza.cobranzas.interaccion.infrastructure.web;

import coovitelCobranza.cobranzas.interaccion.application.dto.ActualizarResultadoInteraccionRequest;
import coovitelCobranza.cobranzas.interaccion.application.dto.CrearInteraccionRequest;
import coovitelCobranza.cobranzas.interaccion.application.dto.GetInteractionByIdRequest;
import coovitelCobranza.cobranzas.interaccion.application.dto.InteraccionResponse;
import coovitelCobranza.cobranzas.interaccion.application.dto.ListInteractionsByCaseRequest;
import coovitelCobranza.cobranzas.interaccion.application.dto.UpdateInteractionResultRequest;
import coovitelCobranza.cobranzas.interaccion.application.service.InteractionApplicationService;
import coovitelCobranza.cobranzas.interaccion.application.dto.CreateInteractionRequest;
import coovitelCobranza.cobranzas.interaccion.application.dto.InteractionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interactions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InteraccionController {

    private final InteractionApplicationService interactionApplicationService;

    public InteraccionController(InteractionApplicationService interactionApplicationService) {
        this.interactionApplicationService = interactionApplicationService;
    }

    @PostMapping
    public ResponseEntity<InteractionResponse> create(@RequestBody CreateInteractionRequest request) {
        InteractionResponse response = interactionApplicationService.createInteraction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/search/id")
    public ResponseEntity<InteractionResponse> getById(@RequestBody GetInteractionByIdRequest request) {
        InteractionResponse response = interactionApplicationService.getById(request.interactionId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/search/case")
    public ResponseEntity<List<InteractionResponse>> listByCase(@RequestBody ListInteractionsByCaseRequest request) {
        List<InteractionResponse> response = interactionApplicationService.listByCase(request.caseId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-result")
    public ResponseEntity<InteractionResponse> updateResult(@RequestBody UpdateInteractionResultRequest request) {
        InteractionResponse response = interactionApplicationService.updateResult(
                request.interactionId(),
                request
        );
        return ResponseEntity.ok(response);
    }
}

