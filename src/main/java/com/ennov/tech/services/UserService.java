package com.ennov.tech.services;

import com.ennov.tech.domains.AppUser;
import com.ennov.tech.domains.AuthenticationRequest;
import com.ennov.tech.domains.Ticket;
import com.ennov.tech.domains.dtos.UserDto;

import java.util.List;

public interface UserService {

    public List<AppUser> search();
    public void create(UserDto user);
    public String authenticate(AuthenticationRequest request);
    public void update(Long userIdToUpdate, UserDto dto);
    public List<Ticket> userTickets(Long userId);
}
