package coovitelCobranza.cobranzas.casogestion.application.service;

import coovitelCobranza.cobranzas.auditoria.domain.service.AuditService;
import coovitelCobranza.cobranzas.casogestion.application.dto.AssignAdvisorRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.CaseResponse;
import coovitelCobranza.cobranzas.casogestion.application.dto.CreateCaseRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.ScheduleActionRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.TransitionCaseStatusRequest;
import coovitelCobranza.cobranzas.casogestion.application.exception.CaseBusinessException;
import coovitelCobranza.cobranzas.casogestion.application.exception.CaseNotFoundException;
import coovitelCobranza.cobranzas.casogestion.domain.model.CaseAssignmentTrace;
import coovitelCobranza.cobranzas.casogestion.domain.model.Case;
import coovitelCobranza.cobranzas.casogestion.domain.repository.CaseAssignmentTraceRepository;
import coovitelCobranza.cobranzas.casogestion.domain.repository.CaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Application service for case lifecycle operations with full audit tracing.
 */
@Service
public class CaseApplicationService {

    private static final String MODULE_CASE_MANAGEMENT = "CASE_MANAGEMENT";

    private final CaseRepository caseRepository;
    private final CaseAssignmentTraceRepository assignmentTraceRepository;
    private final AuditService auditService;

    public CaseApplicationService(CaseRepository caseRepository,
                                  CaseAssignmentTraceRepository assignmentTraceRepository,
                                  AuditService auditService) {
        this.caseRepository = caseRepository;
        this.assignmentTraceRepository = assignmentTraceRepository;
        this.auditService = auditService;
    }

    @Transactional
    public CaseResponse createCase(CreateCaseRequest request) {
        try {
            Case.Priority priority = Case.Priority.valueOf(request.priority());
            Case caseEntity = Case.create(request.obligationId(), priority);
            Case savedCase = caseRepository.save(caseEntity);
            auditService.registerEvent(
                    MODULE_CASE_MANAGEMENT,
                    "CASE",
                    savedCase.getId(),
                    "CASE_CREATED",
                    "system",
                    null,
                    "SYSTEM",
                    "Case created for obligation " + savedCase.getObligationId() + " with priority " + savedCase.getPriority(),
                    null
            );
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
            String assignmentSource = request.assignmentSource() != null ? request.assignmentSource() : "MANUAL";

            assignmentTraceRepository.save(CaseAssignmentTrace.create(
                    updatedCase.getId(),
                    request.advisor(),
                    assignmentSource,
                    request.performedBy(),
                    request.performedByRole(),
                    request.correlationId()
            ));

            auditService.registerEvent(
                    MODULE_CASE_MANAGEMENT,
                    "CASE",
                    updatedCase.getId(),
                    "CASE_ASSIGNED_" + assignmentSource,
                    request.performedBy() != null ? request.performedBy() : "system",
                    request.performedByRole(),
                    assignmentSource,
                    "Advisor " + request.advisor() + " assigned to case",
                    request.correlationId()
            );
            return CaseResponse.fromDomain(updatedCase);
        } catch (IllegalArgumentException | IllegalStateException e) {
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
            auditService.registerEvent(
                    MODULE_CASE_MANAGEMENT,
                    "CASE",
                    updatedCase.getId(),
                    "CASE_ACTION_SCHEDULED",
                    "system",
                    null,
                    "SYSTEM",
                    "Next action scheduled at " + request.dateTime(),
                    null
            );
            return CaseResponse.fromDomain(updatedCase);
        } catch (NullPointerException | IllegalStateException e) {
            throw new CaseBusinessException(e.getMessage());
        }
    }

    @Transactional
    public CaseResponse transitionStatus(Long id, TransitionCaseStatusRequest request) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(id));
        try {
            Case.Status targetStatus = Case.Status.valueOf(request.targetStatus());
            caseEntity.transitionTo(targetStatus, request.reason());
            Case updatedCase = caseRepository.save(caseEntity);
            auditService.registerEvent(
                    MODULE_CASE_MANAGEMENT,
                    "CASE",
                    updatedCase.getId(),
                    "CASE_STATUS_CHANGED",
                    request.performedBy() != null ? request.performedBy() : "system",
                    request.performedByRole(),
                    request.source() != null ? request.source() : "MANUAL",
                    "Transitioned to " + targetStatus + " reason=" + request.reason(),
                    request.correlationId()
            );
            return CaseResponse.fromDomain(updatedCase);
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new CaseBusinessException(e.getMessage());
        }
    }

    @Transactional
    public CaseResponse closeCase(Long id) {
        Case caseEntity = caseRepository.findById(id)
                .orElseThrow(() -> new CaseNotFoundException(id));
        caseEntity.close();
        Case updatedCase = caseRepository.save(caseEntity);
        auditService.registerEvent(
                MODULE_CASE_MANAGEMENT,
                "CASE",
                updatedCase.getId(),
                "CASE_CLOSED",
                "system",
                null,
                "SYSTEM",
                "Case closed",
                null
        );
        return CaseResponse.fromDomain(updatedCase);
    }
}
