package cooviteCobranza.cobranzas.cliente.application.service;

import cooviteCobranza.cobranzas.cliente.application.dto.ActualizarContactoClienteRequest;
import cooviteCobranza.cobranzas.cliente.application.dto.ActualizarConsentimientosClienteRequest;
import cooviteCobranza.cobranzas.cliente.application.dto.ClienteResponse;
import cooviteCobranza.cobranzas.cliente.application.dto.CrearClienteRequest;
import cooviteCobranza.cobranzas.cliente.application.exception.ClienteBusinessException;
import cooviteCobranza.cobranzas.cliente.application.exception.ClienteNotFoundException;
import cooviteCobranza.cobranzas.cliente.domain.model.Cliente;
import cooviteCobranza.cobranzas.cliente.domain.repository.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteApplicationService {

    private final ClienteRepository clienteRepository;

    public ClienteApplicationService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public ClienteResponse crearCliente(CrearClienteRequest request) {
        try {
            // Validar que el cliente no exista
            var clienteExistente = clienteRepository.findByDocumento(request.tipoDocumento(), request.numeroDocumento());
            if (clienteExistente.isPresent()) {
                throw new ClienteBusinessException(
                        "Cliente ya existe con documento: " + request.tipoDocumento() + " " + request.numeroDocumento()
                );
            }

            // Crear nuevo cliente
            Cliente cliente = Cliente.crear(
                    request.tipoDocumento(),
                    request.numeroDocumento(),
                    request.nombreCompleto()
            );

            // Actualizar contacto si se proporciona
            if (request.telefono() != null || request.email() != null) {
                cliente.actualizarContacto(request.telefono(), request.email());
            }

            Cliente clienteGuardado = clienteRepository.save(cliente);
            return ClienteResponse.fromDomain(clienteGuardado);
        } catch (ClienteBusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new ClienteBusinessException("Error al crear cliente", e);
        }
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtenerPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));
        return ClienteResponse.fromDomain(cliente);
    }

    @Transactional(readOnly = true)
    public ClienteResponse obtenerPorDocumento(String tipoDocumento, String numeroDocumento) {
        Cliente cliente = clienteRepository.findByDocumento(tipoDocumento, numeroDocumento)
                .orElseThrow(() -> new ClienteNotFoundException(tipoDocumento, numeroDocumento));
        return ClienteResponse.fromDomain(cliente);
    }

    @Transactional
    public ClienteResponse actualizarContacto(Long id, ActualizarContactoClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));

        cliente.actualizarContacto(request.telefono(), request.email());
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return ClienteResponse.fromDomain(clienteActualizado);
    }

    @Transactional
    public ClienteResponse actualizarConsentimientos(Long id, ActualizarConsentimientosClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));

        cliente.actualizarConsentimientos(
                request.aceptaWhatsApp(),
                request.aceptaSms(),
                request.aceptaEmail()
        );
        Cliente clienteActualizado = clienteRepository.save(cliente);
        return ClienteResponse.fromDomain(clienteActualizado);
    }
}

