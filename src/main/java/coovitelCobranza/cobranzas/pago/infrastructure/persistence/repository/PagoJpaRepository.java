package coovitelCobranza.cobranzas.pago.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoJpaRepository extends JpaRepository<PagoJpaEntity, Long> {

    Optional<PagoJpaEntity> findByReferenciaExterna(String referenciaExterna);

    List<PagoJpaEntity> findByObligacionId(Long obligacionId);
}

