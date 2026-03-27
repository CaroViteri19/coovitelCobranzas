package coovitelCobranza.cobranzas.obligacion.infrastructure.persistence;

import coovitelCobranza.cobranzas.obligacion.domain.model.Obligacion;
import coovitelCobranza.cobranzas.obligacion.domain.repository.ObligacionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ObligacionRepositoryImpl implements ObligacionRepository {

    private final ObligacionJpaRepository jpaRepository;

    public ObligacionRepositoryImpl(ObligacionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Obligacion save(Obligacion obligacion) {
        ObligacionJpaEntity entity = ObligacionJpaEntity.fromDomain(obligacion);
        ObligacionJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Obligacion> findById(Long id) {
        return jpaRepository.findById(id).map(ObligacionJpaEntity::toDomain);
    }

    @Override
    public Optional<Obligacion> findByNumeroObligacion(String numeroObligacion) {
        return jpaRepository.findByObligationNumber(numeroObligacion).map(ObligacionJpaEntity::toDomain);
    }

    @Override
    public List<Obligacion> findByClienteId(Long clienteId) {
        return jpaRepository.findByCustomerId(clienteId).stream()
                .map(ObligacionJpaEntity::toDomain)
                .toList();
    }
}

