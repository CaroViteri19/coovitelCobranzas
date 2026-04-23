package coovitelCobranza.cobranzas.interaction.infrastructure.web;

import coovitelCobranza.cobranzas.interaction.application.dto.CreateInteractionRequest;
import coovitelCobranza.cobranzas.interaction.application.dto.GetInteractionByIdRequest;
import coovitelCobranza.cobranzas.interaction.application.dto.InteractionResponse;
import coovitelCobranza.cobranzas.interaction.application.dto.ListInteractionsByCaseRequest;
import coovitelCobranza.cobranzas.interaction.application.dto.UpdateInteractionResultRequest;
import coovitelCobranza.cobranzas.interaction.application.service.InteractionApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interactions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InteractionController {

    private final InteractionApplicationService interactionApplicationService;

    public InteractionController(InteractionApplicationService interactionApplicationService) {
        this.interactionApplicationService = interactionApplicationService;
    }

    @PreAuthorize("hasAnyRole('ADMINISTRAT  OR','SUPERVISOR','AGENTE')")
    @PostMapping
    public ResponseEntity<InteractionResponse> create(@RequestBody CreateInteractionRequest request) {
        InteractionResponse response = interactionApplicationService.createInteraction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR','AGENTE','AUDITOR')")
    @PostMapping("/search/id")
    public ResponseEntity<InteractionResponse> getById(@RequestBody GetInteractionByIdRequest request) {
        InteractionResponse response = interactionApplicationService.getById(request.interactionId());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR','AGENTE','AUDITOR')")
    @PostMapping("/search/case")
    public ResponseEntity<List<InteractionResponse>> listByCase(@RequestBody ListInteractionsByCaseRequest request) {
        List<InteractionResponse> response = interactionApplicationService.listByCase(request.caseId());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('ADMINISTRATOR','SUPERVISOR','AGENTE')")
    @PostMapping("/update-result")
    public ResponseEntity<InteractionResponse> updateResult(@RequestBody UpdateInteractionResultRequest request) {
        InteractionResponse response = interactionApplicationService.updateResult(
                request.interactionId(),
                request
        );
        return ResponseEntity.ok(response);
    }
}

