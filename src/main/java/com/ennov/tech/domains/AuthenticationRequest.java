package com.ennov.tech.domains;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthenticationRequest {

    @NotEmpty(message = "unable.to.authenticate.empty.username")
    @NotBlank(message = "unable.to.authenticate.null.username")
    private String username;

    @NotEmpty(message = "unable.to.authenticate.empty.password")
    @NotBlank(message = "unable.to.authenticate.null.password")
    private String password;
}
