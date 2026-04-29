package ro.unibuc.authx.dto;

import lombok.Getter;
import lombok.Setter;

public class ForgotPasswordRequest {
    @Getter
    @Setter
    private String email;

    public ForgotPasswordRequest(){}
}
