package com.ennov.tech.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import com.ennov.tech.domains.Ticket;
import com.ennov.tech.domains.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import com.ennov.tech.services.UserService;
import com.ennov.tech.domains.dtos.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ennov.tech.domains.AuthenticationRequest;

import java.util.List;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Tag(name = "Users API")
public class UserController {

    private final UserService userService;

    @ResponseStatus(OK)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Retrieve all users.", description = "Return a list of the users.")
    public List<AppUser> search() {
        log.debug("Call of retrieve all users");
        return this.userService.search();
    }

    @PostMapping()
    @Operation(summary = "Create an user.", description = "Returns code 201 if successfully created.")
    public ResponseEntity<?> create(@RequestBody @Valid UserDto userDto) {
        log.debug("Call of save user {} ", userDto);
        this.userService.create(userDto);
        return ResponseEntity.status(CREATED).build();
    }

    @ResponseStatus(OK)
    @GetMapping("/{id}/ticket")
    @Operation(summary = "Retrieve all tickets by userId.", description = "Return all tickets by userId.")
    public List<Ticket> getUserTickets(@PathVariable Long id) {
        log.debug("Call of retrieve tickets of user: {} ", id);
        return this.userService.userTickets(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a user by id", description = "Return code 204 if successfully")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @RequestBody UserDto userDto) {
        log.debug("Call of update user with id: {} with new ticket {} ", id, userDto);
        this.userService.update(id, userDto);
        return  ResponseEntity.status(NO_CONTENT).build();
    }

    @PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(OK)
    @Operation(summary = "Login user.", description = "Use username and password to logging. This method return the Token.")
    public String authenticate(@RequestBody @Valid AuthenticationRequest request) {
        log.debug("Call of authenticate username: {} with password: {}", request.getUsername(), request.getPassword());
        return userService.authenticate(request);
    }
}
