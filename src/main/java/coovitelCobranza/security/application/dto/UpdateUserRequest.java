package coovitelCobranza.security.application.dto;

public class UpdateUserRequest {

    private Long userId;

    private Long phone;
    private String email;
    private Boolean enabled;
    public UpdateUserRequest(Long userId, String email, Boolean enabled) {
        this.userId = userId;
        //this.phone = phone;
        this.email = email;
        this.enabled = enabled;
    }
    public UpdateUserRequest() {}

    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
//    public Long getPhone() {
//        return phone;
//    }
//    public void setPhone(Long phone) {
//        this.phone = phone;
//    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

}
