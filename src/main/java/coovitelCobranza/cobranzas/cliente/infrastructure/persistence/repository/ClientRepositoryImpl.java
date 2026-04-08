package coovitelCobranza.cobranzas.cliente.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.cliente.domain.model.Client;
import coovitelCobranza.cobranzas.cliente.domain.repository.ClientRepository;
import coovitelCobranza.cobranzas.cliente.infrastructure.persistence.entity.ClientJpaEntity;

import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Implementación del repositorio de clientes.
 * Proporciona el mapeo entre la capa de persistencia JPA y el modelo de dominio.
 */
@Component
public class ClientRepositoryImpl implements ClientRepository {

    private final ClientJpaRepository jpaRepository;

    /**
     * Construye la implementación del repositorio.
     *
     * @param jpaRepository repositorio JPA para operaciones de persistencia
     */
    public ClientRepositoryImpl(ClientJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    /**
     * Guarda o actualiza un cliente, mapeando desde el modelo de dominio a la entidad JPA.
     *
     * @param cliente cliente del dominio a guardar
     * @return cliente guardado con su identificador asignado
     */
    @Override
    public Client save(Client cliente) {
        ClientJpaEntity entity = clienteToEntity(cliente);
        ClientJpaEntity savedEntity = jpaRepository.save(entity);
        return entityToClient(savedEntity);
    }

    /**
     * Busca un cliente por su identificador único.
     *
     * @param id identificador del cliente
     * @return Optional con el cliente mapeado a dominio
     */
    @Override
    public Optional<Client> findById(Long id) {
        return jpaRepository.findById(id).map(this::entityToClient);
    }

    /**
     * Busca un cliente por tipo y número de documento.
     *
     * @param tipoDocumento tipo de documento de identidad
     * @param numeroDocumento número de documento de identidad
     * @return Optional con el cliente mapeado a dominio
     */
    @Override
    public Optional<Client> findByDocumento(String tipoDocumento, String numeroDocumento) {
        return jpaRepository.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento)
                .map(this::entityToClient);
    }

    /**
     * Convierte un cliente del dominio a entidad JPA.
     *
     * @param cliente cliente del dominio
     * @return entidad JPA mapeada
     */
    private ClientJpaEntity clienteToEntity(Client cliente) {
        return new ClientJpaEntity(
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
    }

    /**
     * Convierte una entidad JPA a cliente del dominio.
     *
     * @param entity entidad JPA
     * @return cliente del dominio mapeado
     */
    private Client entityToClient(ClientJpaEntity entity) {
        return Client.reconstruct(
                entity.getId(),
                entity.getTipoDocumento(),
                entity.getNumeroDocumento(),
                entity.getFullName(),
                entity.getTelefono(),
                entity.getEmail(),
                entity.isAceptaWhatsApp(),
                entity.isAceptaSms(),
                entity.isAceptaEmail(),
                entity.getUpdatedAt()
        );
    }
}

