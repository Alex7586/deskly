package ro.unibuc.deskly.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;

public class AuthResponse {
    @Getter
    @Setter
    private String message;
    @Getter
    @Setter
    private Long userId;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String resetToken;

    public AuthResponse(){}

    public AuthResponse(String message, Long userId, String email){
        this.message = message;
        this.userId = userId;
        this.email = email;
    }

    public AuthResponse(String message, Long userId, String email, String resetToken){
        this.message = message;
        this.userId = userId;
        this.email = email;
        this.resetToken = resetToken;
    }
}
