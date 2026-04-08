package coovitelCobranza.security.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UpdateRoleRequest {

    @NotNull(message = "idUser is required")
    private Long idUser;

    @NotNull(message = "role is required")
    @Size(min = 1, message = "role must contain at least one role id")
    private List<Long> role;
    public UpdateRoleRequest() {}

    public UpdateRoleRequest(Long idUser,  List<Long> role) {
        this.idUser = idUser;
        this.role = role;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public List<Long> getRole() {
        return role;
    }

    public void setRole(List<Long> role) {
        this.role = role;
    }
}
