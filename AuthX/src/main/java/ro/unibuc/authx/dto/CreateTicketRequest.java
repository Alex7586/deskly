package ro.unibuc.authx.dto;

import lombok.Getter;
import lombok.Setter;

public class CreateTicketRequest {
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private String description;
    @Getter
    @Setter
    private String status;
    @Getter
    @Setter
    private String severity;

    public CreateTicketRequest(){}
}
