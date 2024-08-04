package com.ennov.tech.services.mappers;

import com.ennov.tech.domains.Ticket;
import com.ennov.tech.domains.dtos.TicketDto;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class TicketDtoMapper implements Function<TicketDto, Ticket> {

    @Override
    public Ticket apply(TicketDto dto) {
        return new Ticket(dto.titre(), dto.description(), dto.status());
    }
}
