package ro.unibuc.deskly.dto;

import lombok.Getter;
import lombok.Setter;

public class ForgotPasswordRequest {
    @Getter
    @Setter
    private String email;

    public ForgotPasswordRequest(){}
}
