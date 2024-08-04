package com.ennov.tech.services;

import com.ennov.tech.domains.Ticket;
import com.ennov.tech.domains.dtos.TicketDto;

import java.util.List;

public interface TicketService {

    public List<Ticket> search();
    public void delete(long ticketId);
    public void create(TicketDto ticket);
    public void update(Long ticketIdToUpdate, TicketDto ticket);
    public Ticket getTicketById(Long ticketId);
    public void assignTicketToUser(Long ticketId, Long userID);
}
