package com.ennov.tech.controllers;

import com.ennov.tech.domains.AppUser;
import com.ennov.tech.domains.Factory;
import com.ennov.tech.domains.Ticket;
import com.ennov.tech.domains.dtos.TicketDto;
import com.ennov.tech.domains.dtos.UserDto;
import com.ennov.tech.services.TicketService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TicketControllerTest {

    @InjectMocks TicketController underTest;
    @Mock TicketService ticketService;

    @Test
    void search() {
        Ticket ticket1 = Factory.buildTicket();
        Ticket ticket2 = Factory.buildTicket();
        List<Ticket> tickets = List.of(ticket1, ticket2);

        when(this.ticketService.search()).thenReturn(tickets);
        var expected = this.underTest.search();

        assertThat(Objects.equals(expected.size(), 2)).isTrue();
        assertThat(expected.get(0).getTitre()).isEqualTo(ticket1.getTitre());
        assertThat(expected.get(1).getDescription()).isEqualTo(ticket2.getDescription());
    }

    @Test
    void create() {
        // Given
        TicketDto dto = Factory.buildTicketDto();

        // when
        ResponseEntity<?> response = this.underTest.create(dto);

        // Then
        assertThat(Objects.equals(response.getStatusCode().value(), 201)).isTrue();
    }

    @Test
    void getTicketById() {
        // Given
        Long ticketId = 1L;
        Ticket ticket = Factory.buildTicket();
        ticket.setId(ticketId);

        // When
        when(this.ticketService.getTicketById(ticketId)).thenReturn(ticket);
        var expected = this.underTest.getTicketById(ticketId);

        // Then
        assertThat(expected).isNotNull();
        assertThat(Objects.equals(expected.getTitre(), ticket.getTitre())).isTrue();
        assertThat(Objects.equals(expected.getDescription(), ticket.getDescription())).isTrue();
    }

    @Test
    void delete() {
        // Given
        Long ticketId = 5L;

        // when
        ResponseEntity<?> response = this.underTest.delete(ticketId);

        // Then
        assertThat(Objects.equals(response.getStatusCode().value(), 202)).isTrue();
    }

    @Test
    void update() {
        // Given
        Long ticketId = 1L;
        TicketDto dto = Factory.buildTicketDto();

        // When
        ResponseEntity<?> response = this.underTest.update(ticketId, dto);

        // Then
        assertThat(Objects.equals(response.getStatusCode().value(), 204)).isTrue();
    }

    @Test
    void assignTicketToUser() {
        // Given
        Long userId = 2L;
        Long ticketId = 1L;

        // When
        ResponseEntity<?> response = this.underTest.assignTicketToUser(ticketId, userId);

        // Then
        assertThat(Objects.equals(response.getStatusCode().value(), 204)).isTrue();

    }
}