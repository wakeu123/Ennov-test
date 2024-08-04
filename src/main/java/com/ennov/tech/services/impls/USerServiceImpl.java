package com.ennov.tech.services.impls;

import com.ennov.tech.domains.AppUser;
import com.ennov.tech.domains.AuthenticationRequest;
import com.ennov.tech.domains.Ticket;
import com.ennov.tech.domains.dtos.UserDto;
import com.ennov.tech.exceptions.BadRequestException;
import com.ennov.tech.exceptions.ConflictException;
import com.ennov.tech.exceptions.EntityNotFoundException;
import com.ennov.tech.repositories.UserRepository;
import com.ennov.tech.services.JwtService;
import com.ennov.tech.services.UserService;
import com.ennov.tech.services.mappers.UserDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class USerServiceImpl implements UserService {

    private final UserDtoMapper mapper;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public List<AppUser> search() {
        log.debug("Try to retrieve all users");
        return userRepository.findAll();
    }

    @Override
    public void create(UserDto dto) {
        log.debug("Try to save userDto {}", dto);
        if(dto == null) {
            throw new BadRequestException("Unable to save null object");
        }
        Optional<AppUser> user = this.userRepository.findByUsernameOrEmail(dto.username(), dto.email());
        if(user.isPresent()) {
            throw new ConflictException("Duplicate username or email");
        }
        AppUser appUser = mapper.apply(dto);
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        userRepository.save(appUser);
    }

    @Override
    public void update(Long userIdToUpdate, UserDto dto) {
        log.debug("Try to update user with Id {} with value {}", userIdToUpdate, dto);
        if(userIdToUpdate == null || dto == null) {
            throw new BadRequestException("Unable to update object with null value");
        }
        AppUser userSaved = this.lire(userIdToUpdate);
        userSaved.setEmail(dto.email());
        userSaved.setUsername(dto.username());
        userRepository.save(userSaved);
    }

    @Override
    public List<Ticket> userTickets(Long userId) {
        log.debug("Try to retrieve all ticket of userId : {}", userId);
        if (userId == null) {
            throw new BadRequestException("Unable to retrieve object with null id");
        }
        AppUser user= this.lire(userId);
        log.debug("Tickets successfully retrieve.");
        return user.getTickets();
    }

    @Override
    public String authenticate(AuthenticationRequest request) {
        log.debug("Try to authenticate username : {}", request.getUsername());
        if (request.getUsername() == null || request.getPassword() == null) {
            throw new BadRequestException("Username or password not found");
        }
        var auth = this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        var claims = new HashMap<String, Object>();
        var user = ((AppUser)auth.getPrincipal());
        claims.put("email", user.getEmail());
        log.debug("Username successfully authenticated : {} ", request.getUsername());
        return this.jwtService.generateToken(claims, user);
    }

    public AppUser lire(Long userId) {
        return this.userRepository.findById(userId)
                .orElseThrow( () -> new EntityNotFoundException("User not found."));
    }
}
