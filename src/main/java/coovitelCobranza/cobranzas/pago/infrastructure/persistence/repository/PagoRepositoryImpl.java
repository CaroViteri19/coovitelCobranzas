package coovitelCobranza.cobranzas.pago.infrastructure.persistence;

import coovitelCobranza.cobranzas.pago.domain.model.Pago;
import coovitelCobranza.cobranzas.pago.domain.repository.PagoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PagoRepositoryImpl implements PagoRepository {

    private final PagoJpaRepository jpaRepository;

    public PagoRepositoryImpl(PagoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Pago save(Pago pago) {
        PagoJpaEntity entity = pagoToEntity(pago);
        PagoJpaEntity savedEntity = jpaRepository.save(entity);
        return entityToPago(savedEntity);
    }

    @Override
    public Optional<Pago> findById(Long id) {
        return jpaRepository.findById(id).map(this::entityToPago);
    }

    @Override
    public Optional<Pago> findByReferenciaExterna(String referenciaExterna) {
        return jpaRepository.findByReferenciaExterna(referenciaExterna).map(this::entityToPago);
    }

    @Override
    public List<Pago> findByObligacionId(Long obligacionId) {
        return jpaRepository.findByObligacionId(obligacionId).stream()
                .map(this::entityToPago)
                .toList();
    }

    // Mappers
    private PagoJpaEntity pagoToEntity(Pago pago) {
        return new PagoJpaEntity(
                pago.getId(),
                pago.getObligacionId(),
                pago.getValor(),
                pago.getReferenciaExterna(),
                pago.getMetodo().name(),
                pago.getEstado().name(),
                pago.getConfirmadoAt(),
                pago.getCreatedAt()
        );
    }

    private Pago entityToPago(PagoJpaEntity entity) {
        return Pago.reconstruir(
                entity.getId(),
                entity.getObligacionId(),
                entity.getValor(),
                entity.getReferenciaExterna(),
                Pago.MetodoPago.valueOf(entity.getMetodo()),
                Pago.EstadoPago.valueOf(entity.getEstado()),
                entity.getConfirmadoAt(),
                entity.getCreatedAt()
        );
    }
}

