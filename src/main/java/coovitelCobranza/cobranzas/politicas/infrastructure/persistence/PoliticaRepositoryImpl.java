package coovitelCobranza.cobranzas.politicas.infrastructure.persistence;

import coovitelCobranza.cobranzas.politicas.domain.model.Politica;
import coovitelCobranza.cobranzas.politicas.domain.repository.PoliticaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PoliticaRepositoryImpl implements PoliticaRepository {

    private final PoliticaJpaRepository jpaRepository;

    public PoliticaRepositoryImpl(PoliticaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Politica save(Politica politica) {
        PoliticaJpaEntity entity = toEntity(politica);
        PoliticaJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Politica> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Politica> findByEstrategiaId(Long estrategiaId) {
        return jpaRepository.findByEstrategiaId(estrategiaId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Politica> findActivas() {
        return jpaRepository.findByActivaTrue().stream()
                .map(this::toDomain)
                .toList();
    }

    private PoliticaJpaEntity toEntity(Politica politica) {
        return new PoliticaJpaEntity(
                politica.getId(),
                politica.getEstrategiaId(),
                politica.getTipoCobro().name(),
                politica.getDiasMoraMinimo(),
                politica.getDiasMoraMaximo(),
                politica.getAccion(),
                politica.isActiva(),
                politica.getUpdatedAt()
        );
    }

    private Politica toDomain(PoliticaJpaEntity entity) {
        return Politica.reconstruir(
                entity.getId(),
                entity.getEstrategiaId(),
                Politica.TipoCobro.valueOf(entity.getTipoCobro()),
                entity.getDiasMoraMinimo(),
                entity.getDiasMoraMaximo(),
                entity.getAccion(),
                entity.isActiva(),
                entity.getUpdatedAt()
        );
    }
}

