package coovitelCobranza.cobranzas.shared.domain.event;

public interface DomainEventPublisher {

    void publish(DomainEvent event);
}

