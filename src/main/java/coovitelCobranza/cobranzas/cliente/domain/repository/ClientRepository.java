package coovitelCobranza.cobranzas.cliente.domain.repository;

import coovitelCobranza.cobranzas.cliente.domain.model.Client;

import java.util.Optional;

public interface ClientRepository {

    Client save(Client cliente);

    Optional<Client> findById(Long id);

    Optional<Client> findByDocumento(String tipoDocumento, String numeroDocumento);
}

