package coovitelCobranza.cobranzas.cargamasiva.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.cargamasiva.infrastructure.persistence.entity.AsociadoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * Repositorio Spring Data JPA para {@link AsociadoJpaEntity}.
 *
 * <p>Usado exclusivamente para <b>queries de unicidad</b> antes de la inserción.
 * La inserción masiva se delega a {@link AsociadoRepositoryImpl} via JdbcTemplate.
 */
@Repository
public interface AsociadoJpaRepository extends JpaRepository<AsociadoJpaEntity, Long> {

    /**
     * Devuelve los NUM_DOCUMENTO que ya existen en la BD
     * de entre los proporcionados (IN clause).
     *
     * <p>Consulta optimizada: proyecta solo la columna {@code num_documento}
     * sin cargar el resto del objeto.
     */
    @Query("""
            SELECT a.numDocumento
            FROM AsociadoJpaEntity a
            WHERE a.numDocumento IN :documentos
            """)
    Set<String> findDocumentosExistentes(@Param("documentos") Set<String> documentos);

    /**
     * Devuelve los emails que ya existen en la BD (case-insensitive)
     * de entre los proporcionados.
     */
    @Query("""
            SELECT LOWER(a.email)
            FROM AsociadoJpaEntity a
            WHERE LOWER(a.email) IN :emails
              AND a.email IS NOT NULL
            """)
    Set<String> findEmailsExistentes(@Param("emails") Set<String> emails);
}
