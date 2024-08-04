package com.ennov.tech.domains;

import com.ennov.tech.domains.dtos.TicketDto;
import com.ennov.tech.domains.dtos.UserDto;
import com.ennov.tech.domains.enums.TicketStatut;

public class Factory {

    // Users

    public static AppUser buildUser() {
        return new AppUser("test1@test.com", "test1", "password1");
    }

    public static UserDto buildUserDto() {
        return new UserDto(null,"test@test.com", "test", "password");
    }

    public static AuthenticationRequest buildLoginRequest() {
        return AuthenticationRequest.builder()
                .username("test")
                .password("password")
                .build();
    }


    // Tickets

    public static Ticket buildTicket() {
        return new Ticket("Titre du ticket", "description du ticket", TicketStatut.IN_PROGRESS);
    }

    public static TicketDto buildTicketDto() {
        return new TicketDto("Titre du ticket de test", "Description du ticket de test", TicketStatut.COMPLETED);
    }
}
