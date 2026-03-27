package coovitelCobranza.cobranzas.cliente.domain.repository;

import coovitelCobranza.cobranzas.cliente.domain.model.Cliente;

import java.util.Optional;

public interface ClienteRepository {

    Cliente save(Cliente cliente);

    Optional<Cliente> findById(Long id);

    Optional<Cliente> findByDocumento(String tipoDocumento, String numeroDocumento);
}

