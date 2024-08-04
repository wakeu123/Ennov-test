package com.ennov.tech.services.mappers;

import com.ennov.tech.domains.AppUser;
import com.ennov.tech.domains.dtos.UserDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class UserDtoMapper implements Function<UserDto, AppUser> {

    @Override
    public AppUser apply(UserDto dto) {
        return new AppUser(dto.email(), dto.username(), dto.password());
    }
}