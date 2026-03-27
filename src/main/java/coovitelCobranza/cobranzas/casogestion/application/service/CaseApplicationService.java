package coovitelCobranza.cobranzas.casogestion.application.service;

import coovitelCobranza.cobranzas.casogestion.application.dto.AssignAdvisorRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.CaseResponse;
import coovitelCobranza.cobranzas.casogestion.application.dto.CreateCaseRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.ScheduleActionRequest;
import coovitelCobranza.cobranzas.casogestion.application.exception.CaseBusinessException;
import coovitelCobranza.cobranzas.casogestion.application.exception.CaseNotFoundException;
import coovitelCobranza.cobranzas.casogestion.domain.model.CasoGestion;
import coovitelCobranza.cobranzas.casogestion.domain.repository.CasoGestionRepository;
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
 * 1. createCase() → Crear un nuevo caso de cobranza
 * 2. getById() → Obtener un caso por su ID
 * 3. listPending() → Listar todos los casos sin asignar
 * 4. assignAdvisor() → Asignar un asesor a un caso
 * 5. scheduleAction() → Programar siguiente acción en un caso
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

    private final CasoGestionRepository casoGestionRepository;

    public CaseApplicationService(CasoGestionRepository casoGestionRepository) {
        this.casoGestionRepository = casoGestionRepository;
    }

    @Transactional
    public CaseResponse createCase(CreateCaseRequest request) {
        try {
            // Validate priority
            CasoGestion.Prioridad priority = CasoGestion.Prioridad.valueOf(request.priority());

            // Create new case
            CasoGestion casoGestion = CasoGestion.crear(request.obligationId(), priority);

            CasoGestion savedCase = casoGestionRepository.save(casoGestion);
            return CaseResponse.fromDomain(savedCase);
        } catch (IllegalArgumentException e) {
            throw new CaseBusinessException("Invalid priority: " + request.priority());
        } catch (Exception e) {
            throw new CaseBusinessException("Error creating case", e);
        }
    }

    @Transactional(readOnly = true)
    public CaseResponse getById(Long id) {
        CasoGestion casoGestion = casoGestionRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(id));
        return CaseResponse.fromDomain(casoGestion);
    }

    @Transactional(readOnly = true)
    public List<CaseResponse> listPending() {
        return casoGestionRepository.findPendientes().stream()
                .map(CaseResponse::fromDomain)
                .toList();
    }

    @Transactional
    public CaseResponse assignAdvisor(Long id, AssignAdvisorRequest request) {
        CasoGestion casoGestion = casoGestionRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(id));

        try {
            casoGestion.asignarAsesor(request.advisor());
            CasoGestion updatedCase = casoGestionRepository.save(casoGestion);
            return CaseResponse.fromDomain(updatedCase);
        } catch (IllegalArgumentException e) {
            throw new CaseBusinessException(e.getMessage());
        }
    }

    @Transactional
    public CaseResponse scheduleAction(Long id, ScheduleActionRequest request) {
        CasoGestion casoGestion = casoGestionRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(id));

        try {
            casoGestion.programarSiguienteAccion(request.dateTime());
            CasoGestion updatedCase = casoGestionRepository.save(casoGestion);
            return CaseResponse.fromDomain(updatedCase);
        } catch (NullPointerException e) {
            throw new CaseBusinessException("Date and time are required");
        }
    }

    @Transactional
    public CaseResponse closeCase(Long id) {
        CasoGestion casoGestion = casoGestionRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(id));

        casoGestion.cerrar();
        CasoGestion updatedCase = casoGestionRepository.save(casoGestion);
        return CaseResponse.fromDomain(updatedCase);
    }
}


