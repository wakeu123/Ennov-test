package com.ennov.tech.services.impls;

import com.ennov.tech.domains.AppUser;
import com.ennov.tech.domains.Ticket;
import com.ennov.tech.domains.dtos.TicketDto;
import com.ennov.tech.exceptions.BadRequestException;
import com.ennov.tech.exceptions.ConflictException;
import com.ennov.tech.exceptions.EntityNotFoundException;
import com.ennov.tech.repositories.TicketRepository;
import com.ennov.tech.repositories.UserRepository;
import com.ennov.tech.services.TicketService;
import com.ennov.tech.services.mappers.TicketDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketDtoMapper mapper;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    @Override
    public List<Ticket> search() {
        log.debug("Try to retrieve all tickets");
        return ticketRepository.findAll();
    }

    @Override
    public void delete(long ticketId) {
        log.debug("Try to delete ticket by Id");
        var ticket = this.lire(ticketId);
        ticketRepository.delete(ticket);
    }

    @Override
    public void create(TicketDto dto) {
        log.debug("Try to save ticket {}", dto);
        if(dto == null) {
            throw new BadRequestException("Unable to save null object");
        }
        Optional<Ticket> ticket = this.ticketRepository.findByTitre(dto.titre());
        if(ticket.isPresent()) {
            throw new ConflictException("Duplicate ticket title");
        }
        ticketRepository.save(mapper.apply(dto));
    }

    @Override
    public void update(Long ticketId, TicketDto dto) {
        log.debug("Try to update ID {} with ticket {}", ticketId, dto);
        if(ticketId == null) {
            throw new BadRequestException("Unable to update null object");
        }
        Ticket ticket = this.lire(ticketId);
        if(dto == null) {
            throw new BadRequestException("Unable to update ticket with null object");
        }
        ticket.setStatut(dto.status());
        ticket.setTitre(dto.titre());
        ticket.setDescription(dto.description());
        Optional<Ticket> ticketToSave = this.ticketRepository.findByTitre(ticket.getTitre());
        if(ticketToSave.isPresent() && !Objects.equals(ticket.getId(), ticketToSave.get().getId())) {
            throw new ConflictException("Duplicate ticket title");
        }

        ticketRepository.save(ticket);
    }

    @Override
    public Ticket getTicketById(Long ticketId) {
        log.debug("Try to retrieve ticket with ID {}", ticketId);
        return this.lire(ticketId);
    }

    @Override
    public void assignTicketToUser(Long ticketId, Long userID) {
        log.debug("Try to assign ticket Id {} to user ID {}", ticketId, userID);
        Ticket ticket = this.lire(ticketId);
        Optional<AppUser> user = userRepository.findById(userID);
        if(user.isEmpty()) {
            throw new EntityNotFoundException("User to assign ticket not found.");
        }
        user.get().getTickets().add(ticket);
        userRepository.save(user.get());
        ticket.setUser(user.get());
        ticketRepository.save(ticket);
    }

    public Ticket lire(Long ticketId) {
        return this.ticketRepository.findById(ticketId)
                .orElseThrow( () -> new EntityNotFoundException("Ticket not found."));
    }
}
