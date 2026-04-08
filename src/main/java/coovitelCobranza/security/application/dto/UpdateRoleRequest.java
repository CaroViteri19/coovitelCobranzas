package coovitelCobranza.security.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * Clase de solicitud para actualizar los roles de un usuario.
 * Permite asignar uno o más roles a un usuario existente.
 */
public class UpdateRoleRequest {

    /**
     * Identificador del usuario a actualizar (requerido).
     */
    @NotNull(message = "idUser is required")
    private Long idUser;

    /**
     * Lista de identificadores de roles a asignar (mínimo 1 rol requerido).
     */
    @NotNull(message = "role is required")
    @Size(min = 1, message = "role must contain at least one role id")
    private List<Long> role;

    /**
     * Constructor sin argumentos.
     */
    public UpdateRoleRequest() {}

    /**
     * Constructor con argumentos.
     *
     * @param idUser Identificador del usuario.
     * @param role Lista de identificadores de roles.
     */
    public UpdateRoleRequest(Long idUser,  List<Long> role) {
        this.idUser = idUser;
        this.role = role;
    }

    /**
     * Obtiene el identificador del usuario.
     *
     * @return Identificador del usuario.
     */
    public Long getIdUser() {
        return idUser;
    }

    /**
     * Establece el identificador del usuario.
     *
     * @param idUser Identificador del usuario.
     */
    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    /**
     * Obtiene la lista de identificadores de roles.
     *
     * @return Lista de identificadores de roles.
     */
    public List<Long> getRole() {
        return role;
    }

    /**
     * Establece la lista de identificadores de roles.
     *
     * @param role Lista de identificadores de roles.
     */
    public void setRole(List<Long> role) {
        this.role = role;
    }
}
