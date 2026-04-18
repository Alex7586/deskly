package ro.unibuc.deskly.dto;

public class RegisterRequest {
    private String email;
    private String password;

    public RegisterRequest(){}

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
