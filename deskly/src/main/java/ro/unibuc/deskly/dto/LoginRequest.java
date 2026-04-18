package ro.unibuc.deskly.dto;

import lombok.Getter;

public class LoginRequest {
    @Getter
    private String email;
    @Getter
    private String password;

    public LoginRequest(){}

}
