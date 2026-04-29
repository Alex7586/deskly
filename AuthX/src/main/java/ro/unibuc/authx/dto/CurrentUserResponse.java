package ro.unibuc.authx.dto;

import lombok.Getter;
import lombok.Setter;

public class CurrentUserResponse {
    @Getter
    @Setter
    private Long userId;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String role;

    public CurrentUserResponse(){}
    public CurrentUserResponse(Long userId, String email, String role){
        this.userId = userId;
        this.email = email;
        this.role = role;
    }
}
