package com.ennov.tech.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Tickets API")
public class TicketController {

    private final TicketService ticketService;

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    @Operation(summary = "Retrieve all tickets", description = "Return a list of the tickets.")
    public List<Ticket> search() {
        log.debug("Call of retrieve all tickets");
        return this.ticketService.search();
    }

    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create an user", description = "Returns a ticket as per the id")
    public ResponseEntity<?> create(@RequestBody TicketDto ticketDto) {
        log.debug("Call of save ticket {} ", ticketDto);
        this.ticketService.create(ticketDto);
        return ResponseEntity.status(CREATED).build();
    }

    @ResponseStatus(OK)
    @Operation(summary = "Retrieve a ticket by id", description = "Return a ticket as per the id")
    @GetMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Ticket getTicketById(@PathVariable("id") Long id) {
        log.debug("Call of retrieve ticket with id: {}", id);
        return this.ticketService.getTicketById(id);
    }

    @DeleteMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Delete a ticket by id", description = "Return code 202 if successfully")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        log.debug("Call of delete ticket with id: {} ", id);
        this.ticketService.delete(id);
        return ResponseEntity.status(ACCEPTED).build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update a ticket by id", description = "Return code 204 if successfully")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody TicketDto ticketDto) {
        log.debug("Call of update ticket with id: {} with new ticket {} ", id, ticketDto);
        this.ticketService.update(id, ticketDto);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    @PutMapping("/{ticketId}/assign/{userId}")
    @Operation(summary = "Assign a ticket to an user", description = "Provide a ticketID and userID")
    public ResponseEntity<?> assignTicketToUser(@PathVariable("ticketId") Long ticketId, @PathVariable("userId") Long userId) {
        log.debug("Call of assign ticket id {} to user id: {}", ticketId, userId);
        this.ticketService.assignTicketToUser(ticketId, userId);
        return ResponseEntity.status(NO_CONTENT).build();
    }
}
