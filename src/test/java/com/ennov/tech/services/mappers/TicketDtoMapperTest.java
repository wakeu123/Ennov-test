package com.ennov.tech.services.mappers;

import com.ennov.tech.domains.Factory;
import com.ennov.tech.domains.Ticket;
import com.ennov.tech.domains.dtos.TicketDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class TicketDtoMapperTest {

    private TicketDtoMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new TicketDtoMapper();
    }

    @Test
    void shouldMapTicketDtoToTicket() {
        TicketDto ticketDto = Factory.buildTicketDto();
        Ticket ticket = mapper.apply(ticketDto);
        assertThat(ticket).isNotNull();
        assertThat(ticket.getTitre()).isEqualTo(ticketDto.titre());
        assertThat(ticket.getStatut()).isEqualTo(ticketDto.status());
        assertThat(ticket.getDescription()).isEqualTo(ticketDto.description());
    }
}
