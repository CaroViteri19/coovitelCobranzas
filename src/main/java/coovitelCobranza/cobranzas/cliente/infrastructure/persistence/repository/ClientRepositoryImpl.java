package coovitelCobranza.cobranzas.cliente.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.cliente.domain.model.Client;
import coovitelCobranza.cobranzas.cliente.domain.repository.ClientRepository;
import coovitelCobranza.cobranzas.cliente.infrastructure.persistence.entity.ClientJpaEntity;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del repositorio de clientes.
 * Proporciona el mapeo entre la capa de persistencia JPA y el modelo de dominio.
 */
@Component
public class ClientRepositoryImpl implements ClientRepository {

    private final ClientJpaRepository jpaRepository;

    public ClientRepositoryImpl(ClientJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Client save(Client cliente) {
        ClientJpaEntity entity = clienteToEntity(cliente);
        ClientJpaEntity savedEntity = jpaRepository.save(entity);
        return entityToClient(savedEntity);
    }

    @Override
    public Optional<Client> findById(Long id) {
        return jpaRepository.findById(id).map(this::entityToClient);
    }

    @Override
    public Optional<Client> findByDocumento(String tipoDocumento, String numeroDocumento) {
        return jpaRepository.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento)
                .map(this::entityToClient);
    }

    @Override
    public List<Client> findAll(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));
        return jpaRepository.findAll(pageRequest)
                .getContent()
                .stream()
                .map(this::entityToClient)
                .toList();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    // ── Mapeo Dominio → JPA ───────────────────────────────────────────────────

    private ClientJpaEntity clienteToEntity(Client cliente) {
        ClientJpaEntity entity = new ClientJpaEntity(
                cliente.getId(),
                cliente.getTipoDocumento(),
                cliente.getNumeroDocumento(),
                cliente.getFullName(),
                cliente.getTelefono(),
                cliente.getEmail(),
                cliente.isAceptaWhatsApp(),
                cliente.isAceptaSms(),
                cliente.isAceptaEmail(),
                cliente.getUpdatedAt()
        );
        // Campos extendidos — carga batch
        entity.setTelefono2(cliente.getTelefono2());
        entity.setCiudad(cliente.getCiudad());
        entity.setCanalPreferido(cliente.getCanalPreferido());
        return entity;
    }

    // ── Mapeo JPA → Dominio ───────────────────────────────────────────────────

    private Client entityToClient(ClientJpaEntity entity) {
        return Client.reconstructFull(
                entity.getId(),
                entity.getTipoDocumento(),
                entity.getNumeroDocumento(),
                entity.getFullName(),
                entity.getTelefono(),
                entity.getEmail(),
                entity.getTelefono2(),
                entity.getCiudad(),
                entity.getCanalPreferido(),
                entity.isAceptaWhatsApp(),
                entity.isAceptaSms(),
                entity.isAceptaEmail(),
                entity.getUpdatedAt()
        );
    }
}
