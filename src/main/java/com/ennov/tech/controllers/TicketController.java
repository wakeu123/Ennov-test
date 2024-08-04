package com.ennov.tech.controllers;

import lombok.extern.slf4j.Slf4j;
import com.ennov.tech.domains.Ticket;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import com.ennov.tech.domains.dtos.TicketDto;
import com.ennov.tech.services.TicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping(value = "tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    public List<Ticket> search() {
        log.debug("Call of retrieve all tickets");
        return this.ticketService.search();
    }

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestBody TicketDto ticketDto) {
        log.debug("Call of save ticket {} ", ticketDto);
        this.ticketService.create(ticketDto);
        return ResponseEntity.status(CREATED).build();
    }

    @ResponseStatus(OK)
    @GetMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Ticket getTicketById(@PathVariable("id") Long id) {
        log.debug("Call of retrieve ticket with id: {}", id);
        return this.ticketService.getTicketById(id);
    }

    @DeleteMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.debug("Call of delete ticket with id: {} ", id);
        this.ticketService.delete(id);
        return ResponseEntity.status(ACCEPTED).build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody TicketDto ticketDto) {
        log.debug("Call of update ticket with id: {} with new ticket {} ", id, ticketDto);
        this.ticketService.update(id, ticketDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PutMapping("/{ticketId}/assign/{userId}")
    public ResponseEntity<?> assignTicketToUser(@PathVariable("ticketId") Long ticketId, @PathVariable("userId") Long userId) {
        log.debug("Call of assign ticket id {} to user id: {}", ticketId, userId);
        this.ticketService.assignTicketToUser(ticketId, userId);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}
