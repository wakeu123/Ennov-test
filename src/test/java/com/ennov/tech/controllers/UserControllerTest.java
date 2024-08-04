package com.ennov.tech.controllers;


import com.ennov.tech.domains.AppUser;
import com.ennov.tech.domains.AuthenticationRequest;
import com.ennov.tech.domains.Factory;
import com.ennov.tech.domains.Ticket;
import com.ennov.tech.domains.dtos.UserDto;
import com.ennov.tech.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    UserController underTest;

    @Mock
    UserService userService;

    @Mock MockMvc mockMvc;


    @Test
    public void search() {
        AppUser user1 = Factory.buildUser();
        AppUser user2 = Factory.buildUser();
        List<AppUser> users = List.of(user1, user2);

        when(this.userService.search()).thenReturn(users);
        var expected = this.underTest.search();

        assertThat(Objects.equals(expected.size(), 2)).isTrue();
        assertThat(expected.get(0).getEmail()).isEqualTo(user1.getEmail());
        assertThat(expected.get(1).getEmail()).isEqualTo(user2.getEmail());
    }

    @Test
    public void create() {
        // Given
        UserDto dto = Factory.buildUserDto();

        // when
        ResponseEntity<?> response = this.underTest.create(dto);

        // Then
        assertThat(Objects.equals(response.getStatusCode().value(), 201)).isTrue();
    }

    @Test
    public void searchTicketsByUserId() {
        // Given
        Long userId = 1L;
        Ticket ticket1 = Factory.buildTicket();
        Ticket ticket2 = Factory.buildTicket();
        List<Ticket> tickets = List.of(ticket1, ticket2);

        // When
        when(this.userService.userTickets(userId)).thenReturn(tickets);
        var expected = this.underTest.getUserTickets(userId);

        // Then
        assertThat(Objects.equals(expected.size(), 2)).isTrue();
    }

    @Test
    public void update() {
        // Given
        Long userId = 1L;
        UserDto dto = Factory.buildUserDto();

        // When
        ResponseEntity<?> response = this.underTest.update(userId, dto);

        // Then
        assertThat(Objects.equals(response.getStatusCode().value(), 204)).isTrue();
    }

    @Test
    public void login() {
        // Given
        String token = "eyjfhfiewhfuiwewefhj.ioewfuoewoiwefewyewyuweifweuifu.wifyiwefiweyfy8ef8wef8weft8fwftwefwteufewufywetfuwe";
        AuthenticationRequest request = Factory.buildLoginRequest();

        // When
        when(this.userService.authenticate(request)).thenReturn(token);
        var expected = this.underTest.authenticate(request);

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.length() > 30).isTrue();

    }
}