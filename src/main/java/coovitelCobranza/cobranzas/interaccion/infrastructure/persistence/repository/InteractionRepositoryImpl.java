package coovitelCobranza.cobranzas.interaction.infrastructure.persistence;

import coovitelCobranza.cobranzas.interaction.domain.model.Interaction;
import coovitelCobranza.cobranzas.interaction.domain.repository.InteractionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class InteractionRepositoryImpl implements InteractionRepository {

    private final InteractionJpaRepository jpaRepository;

    public InteractionRepositoryImpl(InteractionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Interaction save(Interaction interaction) {
        InteractionJpaEntity entity = interactionToEntity(interaction);
        InteractionJpaEntity savedEntity = jpaRepository.save(entity);
        return entityToInteraction(savedEntity);
    }

    @Override
    public Optional<Interaction> findById(Long id) {
        return jpaRepository.findById(id).map(this::entityToInteraction);
    }

    @Override
    public List<Interaction> findByCaseId(Long caseId) {
        return jpaRepository.findByCasoGestionId(caseId).stream()
                .map(this::entityToInteraction)
                .toList();
    }

    private InteractionJpaEntity interactionToEntity(Interaction interaction) {
        return new InteractionJpaEntity(
                interaction.getId(),
                interaction.getCaseId(),
                interaction.getChannel().name(),
                interaction.getTemplate(),
                interaction.getResultStatus().name(),
                interaction.getCreatedAt()
        );
    }

    private Interaction entityToInteraction(InteractionJpaEntity entity) {
        return Interaction.reconstruct(
                entity.getId(),
                entity.getCaseId(),
                Interaction.Channel.valueOf(entity.getChannel()),
                entity.getTemplate(),
                Interaction.ResultStatus.valueOf(entity.getResultStatus()),
                entity.getCreatedAt()
        );
    }
}
