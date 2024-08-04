package com.ennov.tech.domains.dtos;

import lombok.Builder;

@Builder
public record UserDto (
        Long id,
        String email,
        String username,
        String password
){}
