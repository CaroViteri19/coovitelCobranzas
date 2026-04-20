package coovitelCobranza.cobranzas.client.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import coovitelCobranza.cobranzas.client.infrastructure.persistence.entity.ClientJpaEntity;

import java.util.Optional;

/**
 * Repositorio JPA para operaciones de persistencia de entidades de clientes.
 * Extiende JpaRepository para operaciones CRUD estándar.
 */
@Repository
public interface ClientJpaRepository extends JpaRepository<ClientJpaEntity, Long> {

    /**
     * Busca un cliente por tipo y número de documento.
     *
     * @param tipoDocumento tipo de documento de identidad
     * @param numeroDocumento número de documento de identidad
     * @return Optional con la entidad de cliente si existe
     */
    Optional<ClientJpaEntity> findByTipoDocumentoAndNumeroDocumento(String tipoDocumento, String numeroDocumento);

    Optional<ClientJpaEntity> findByNumeroDocumento(String numeroDocumento);
}

