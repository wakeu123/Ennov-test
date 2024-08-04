package com.ennov.tech.domains.dtos;

import com.ennov.tech.domains.enums.TicketStatut;

public record TicketDto (
    String titre,
    String description,
    TicketStatut status
){}
