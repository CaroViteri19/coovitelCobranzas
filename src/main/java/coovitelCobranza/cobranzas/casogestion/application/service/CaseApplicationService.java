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
 * Servicio de aplicación que coordina la lógica de negocio para la gestión
 * de casos de cobranza.
 *
 * Actúa como intermediario entre los controladores REST, el dominio de negocio
 * y la capa de persistencia. Proporciona operaciones transaccionales para crear,
 * consultar, asignar asesores, programar acciones y cerrar casos.
 *
 * Responsabilidades:
 * - createCase(): Crear un nuevo caso de cobranza
 * - getById(): Obtener un caso por su ID
 * - listPending(): Listar todos los casos pendientes
 * - assignAdvisor(): Asignar un asesor a un caso
 * - scheduleAction(): Programar la próxima acción en un caso
 * - closeCase(): Cerrar un caso resuelto
 */
@Service
public class CaseApplicationService {

    private final CaseRepository caseRepository;

    /**
     * Construye una instancia del servicio de aplicación.
     *
     * @param caseRepository el repositorio para acceder a los casos
     */
    public CaseApplicationService(CaseRepository caseRepository) {
        this.caseRepository = caseRepository;
    }

    /**
     * Crea un nuevo caso de cobranza.
     *
     * @param request DTO con la información del caso a crear (obligationId y priority)
     * @return DTO con la información del caso creado
     * @throws CaseBusinessException si la prioridad es inválida o hay un error en la creación
     */
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

    /**
     * Obtiene un caso por su identificador único.
     *
     * @param id el ID del caso a recuperar
     * @return DTO con la información del caso
     * @throws CaseNotFoundException si el caso no existe
     */
    @Transactional(readOnly = true)
    public CaseResponse getById(Long id) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(id));
        return CaseResponse.fromDomain(caseEntity);
    }

    /**
     * Lista todos los casos pendientes de gestión.
     *
     * @return lista de DTOs con los casos pendientes
     */
    @Transactional(readOnly = true)
    public List<CaseResponse> listPending() {
        return caseRepository.findPendientes().stream()
                .map(CaseResponse::fromDomain)
                .toList();
    }

    /**
     * Asigna un asesor a un caso existente.
     *
     * @param id el ID del caso a actualizar
     * @param request DTO con el nombre del asesor
     * @return DTO con la información actualizada del caso
     * @throws CaseNotFoundException si el caso no existe
     * @throws CaseBusinessException si el nombre del asesor es inválido
     */
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

    /**
     * Programa la próxima acción para un caso.
     *
     * @param id el ID del caso a actualizar
     * @param request DTO con la fecha y hora de la próxima acción
     * @return DTO con la información actualizada del caso
     * @throws CaseNotFoundException si el caso no existe
     * @throws CaseBusinessException si la fecha y hora son inválidas
     */
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

    /**
     * Cierra un caso de cobranza marcándolo como resuelto.
     *
     * @param id el ID del caso a cerrar
     * @return DTO con la información del caso cerrado
     * @throws CaseNotFoundException si el caso no existe
     */
    @Transactional
    public CaseResponse closeCase(Long id) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(id));
        caseEntity.close();
        Case updatedCase = caseRepository.save(caseEntity);
        return CaseResponse.fromDomain(updatedCase);
    }
}
