package ro.unibuc.authx.dto;

import lombok.Getter;
import lombok.Setter;

public class ResetPasswordRequest {
    @Getter
    @Setter
    private String token;
    @Getter
    @Setter
    private String newPassword;

    public ResetPasswordRequest(){}
}
