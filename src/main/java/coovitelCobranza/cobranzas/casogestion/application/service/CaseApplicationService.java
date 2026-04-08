package coovitelCobranza.cobranzas.casogestion.application.service;

import coovitelCobranza.cobranzas.casogestion.application.dto.AssignAdvisorRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.CaseResponse;
import coovitelCobranza.cobranzas.casogestion.application.dto.CreateCaseRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.ScheduleActionRequest;
import coovitelCobranza.cobranzas.casogestion.application.exception.CaseBusinessException;
import coovitelCobranza.cobranzas.casogestion.application.exception.CaseNotFoundException;
import coovitelCobranza.cobranzas.casogestion.domain.model.Case;
import coovitelCobranza.cobranzas.casogestion.domain.repository.CaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 🎯 SERVICIO DE APLICACIÓN: GESTIÓN DE CASOS (VERSIÓN EN INGLÉS)
 * 
 * Este servicio coordina toda la lógica de negocio para casos de cobranza.
 * Actúa como intermediario entre:
 *   - Los CONTROLLERS (que reciben solicitudes HTTP)
 *   - El DOMINIO (donde está la lógica pura de negocio)
 *   - El REPOSITORIO (que guarda en base de datos)
 * 
 * RESPONSABILIDADES PRINCIPALES:
 * 1. createCase() → Create un nuevo caso de cobranza
 * 2. getById() → Obtener un caso por su ID
 * 3. listPending() → Listar todos los casos sin asignar
 * 4. assignAdvisor() → Assign un asesor a un caso
 * 5. scheduleAction() → Schedule siguiente acción en un caso
 * 6. closeCase() → Cerrar un caso (cuando se resuelve)
 * 
 * EJEMPLO DE USO DESDE UN CONTROLLER:
 *   @PostMapping
 *   public ResponseEntity<CaseResponse> create(@RequestBody CreateCaseRequest request) {
 *       // El controller llama al servicio
 *       CaseResponse response = caseApplicationService.createCase(request);
 *       return ResponseEntity.status(HttpStatus.CREATED).body(response);
 *   }
 * 
 * TRANSACCIONES:
 * - @Transactional: Asegura que la operación sea atómica (todo o nada)
 * - @Transactional(readOnly = true): Para operaciones de solo lectura
 * 
 * VALIDACIONES AUTOMÁTICAS:
 * - Si el caso no existe → Lanza CaseNotFoundException
 * - Si los datos son inválidos → Lanza CaseBusinessException
 */
@Service
public class CaseApplicationService {

    private final CaseRepository caseRepository;

    public CaseApplicationService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    @Transactional
    public CaseResponse createCase(CreateCaseRequest request) {
        try {
            Case.Priority priority = Case.Priority.valueOf(request.priority());
            Case caseEntity = Case.create(request.obligationId(), priority);
            Case savedCase = caseRepository.save(caseEntity);
            return CaseResponse.fromDomain(savedCase);
        } catch (IllegalArgumentException e) {
            throw new CaseBusinessException("Invalid priority: " + request.priority());
        } catch (Exception e) {
            throw new CaseBusinessException("Error creating case", e);
        }
    }

    @Transactional(readOnly = true)
    public CaseResponse getById(Long id) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(id));
        return CaseResponse.fromDomain(caseEntity);
    }

    @Transactional(readOnly = true)
    public List<CaseResponse> listPending() {
        return caseRepository.findPendientes().stream()
                .map(CaseResponse::fromDomain)
                .toList();
    }

    @Transactional
    public CaseResponse assignAdvisor(Long id, AssignAdvisorRequest request) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(id));
        try {
            caseEntity.assignAdvisor(request.advisor());
            Case updatedCase = caseRepository.save(caseEntity);
            return CaseResponse.fromDomain(updatedCase);
        } catch (IllegalArgumentException e) {
            throw new CaseBusinessException(e.getMessage());
        }
    }

    @Transactional
    public CaseResponse scheduleAction(Long id, ScheduleActionRequest request) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(id));
        try {
            caseEntity.scheduleNextAction(request.dateTime());
            Case updatedCase = caseRepository.save(caseEntity);
            return CaseResponse.fromDomain(updatedCase);
        } catch (NullPointerException e) {
            throw new CaseBusinessException("Date and time are required");
        }
    }

    @Transactional
    public CaseResponse closeCase(Long id) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(id));
        caseEntity.close();
        Case updatedCase = caseRepository.save(caseEntity);
        return CaseResponse.fromDomain(updatedCase);
    }
}
