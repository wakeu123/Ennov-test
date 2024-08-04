package com.ennov.tech.services.mappers;

import com.ennov.tech.domains.AppUser;
import com.ennov.tech.domains.Factory;
import com.ennov.tech.domains.dtos.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


class UserDtoMapperTest {

    private UserDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new UserDtoMapper();
    }

    @Test
    void shouldMapUserDtoToAppUser() {
        UserDto userDto = Factory.buildUserDto();
        AppUser user = mapper.apply(userDto);
        assertThat(user).isNotNull();
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getEmail()).isEqualTo(userDto.email());
        assertThat(user.getUsername()).isEqualTo(userDto.username());
        assertThat(user.getPassword()).isEqualTo(userDto.password());
    }
}