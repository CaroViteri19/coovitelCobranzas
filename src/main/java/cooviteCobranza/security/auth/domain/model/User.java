package cooviteCobranza.security.auth.domain.model;

public class User {
    private Long idUser;
    private String name;
    private String Lastname;
    private String email;
    private String password;
    private String role;

    public User() {
    }

    public User(Long idUser, String name, String lastname, String email, String password, String role) {
        this.idUser = idUser;
        this.name = name;
        Lastname = lastname;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return Lastname;
    }

    public void setLastname(String lastname) {
        Lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
