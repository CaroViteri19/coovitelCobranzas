package coovitelCobranza.cobranzas.cliente.domain.repository;

import coovitelCobranza.cobranzas.cliente.domain.model.Client;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de persistencia para clientes.
 * Abstrae la capa de acceso a datos del modelo de dominio.
 */
public interface ClientRepository {

    /**
     * Guarda o actualiza un cliente en la persistencia.
     *
     * @param cliente cliente a guardar
     * @return cliente guardado con su identificador asignado
     */
    Client save(Client cliente);

    /**
     * Busca un cliente por su identificador único.
     *
     * @param id identificador del cliente
     * @return Optional con el cliente si existe, o vacío si no
     */
    Optional<Client> findById(Long id);

    /**
     * Busca un cliente por tipo y número de documento.
     *
     * @param tipoDocumento tipo de documento de identidad
     * @param numeroDocumento número de documento de identidad
     * @return Optional con el cliente si existe, o vacío si no
     */
    Optional<Client> findByDocumento(String tipoDocumento, String numeroDocumento);

    /**
     * Retorna una página de clientes ordenados por ID ascendente.
     *
     * @param page índice de página (0-based)
     * @param size tamaño de página
     * @return clientes de la página solicitada
     */
    List<Client> findAll(int page, int size);

    /**
     * Cuenta el total de clientes almacenados.
     *
     * @return número total de clientes
     */
    long count();
}

