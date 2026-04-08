package coovitelCobranza.cobranzas.cliente.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import coovitelCobranza.cobranzas.cliente.infrastructure.persistence.entity.ClientJpaEntity;

import java.util.Optional;

@Repository
public interface ClientJpaRepository extends JpaRepository<ClientJpaEntity, Long> {

    Optional<ClientJpaEntity> findByTipoDocumentoAndNumeroDocumento(String tipoDocumento, String numeroDocumento);
}

