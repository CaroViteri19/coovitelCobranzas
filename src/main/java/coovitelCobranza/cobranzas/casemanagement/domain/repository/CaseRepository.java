package coovitelCobranza.cobranzas.casemanagement.domain.repository;

import coovitelCobranza.cobranzas.casemanagement.domain.model.Case;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz del repositorio de dominio para la persistencia de casos de cobranza.
 *
 * Define los contratos para acceder y gestionar la persistencia de casos
 * sin depender de detalles de implementación específicos.
 */
public interface CaseRepository {

    /**
     * Guarda un caso de cobranza (crea o actualiza).
     *
     * @param casoGestion el caso a guardar
     * @return el caso guardado con su ID asignado
     */
    Case save(Case casoGestion);

    /**
     * Busca un caso por su identificador único.
     *
     * @param id el ID del caso
     * @return un Optional con el caso si existe, vacío en caso contrario
     */
    Optional<Case> findById(Long id);

    /**
     * Encuentra todos los casos pendientes de gestión.
     *
     * @return lista de casos con estado OPEN o IN_MANAGEMENT
     */
    List<Case> findPendientes();
}

