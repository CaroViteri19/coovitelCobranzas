package coovitelCobranza.cobranzas.bulkimport.domain.repository;

import coovitelCobranza.cobranzas.bulkimport.domain.model.AssociateImport;

import java.util.List;
import java.util.Set;

/**
 * Puerto (port) del repositorio de AssociateImport.
 *
 * <p>Define el contrato de acceso a datos desde el dominio.
 * La implementación concreta vive en la capa de infraestructura.
 *
 * <p>Evolución futura: puede extenderse con paginación reactiva (Flux)
 * cuando se migre a procesamiento asíncrono.
 */
public interface AssociateImportRepository {

    /**
     * Inserta una lista de asociados en batch optimizado.
     * Utiliza JdbcTemplate.batchUpdate() en chunks de {@code chunkSize}.
     *
     * @param asociados lista de modelos de dominio a persistir
     * @param chunkSize tamaño de cada bloque de inserción
     */
    void batchInsert(List<AssociateImport> asociados, int chunkSize);

    /**
     * Verifica cuáles documentos ya existen en la BD.
     *
     * @param documentos conjunto de NUM_DOCUMENTO a verificar
     * @return subconjunto de documentos que ya están en la BD
     */
    Set<String> findExistingDocumentos(Set<String> documentos);

    /**
     * Verifica cuáles emails ya existen en la BD (ignorando nulls).
     *
     * @param emails conjunto de emails a verificar
     * @return subconjunto de emails que ya están en la BD
     */
    Set<String> findExistingEmails(Set<String> emails);

    /**
     * Elimina todos los registros de la tabla (útil para re-carga completa).
     * En producción, esto se controla por permisos de rol.
     */
    void deleteAll();
}
