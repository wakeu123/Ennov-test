package com.ennov.tech.services.impls;

import com.ennov.tech.domains.AppUser;
import com.ennov.tech.domains.AuthenticationRequest;
import com.ennov.tech.domains.Factory;
import com.ennov.tech.domains.Ticket;
import com.ennov.tech.domains.dtos.UserDto;
import com.ennov.tech.exceptions.BadRequestException;
import com.ennov.tech.exceptions.ConflictException;
import com.ennov.tech.exceptions.EntityNotFoundException;
import com.ennov.tech.repositories.UserRepository;
import com.ennov.tech.services.mappers.UserDtoMapper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class USerServiceImplTest {

    @InjectMocks
    private USerServiceImpl underTest;

    @Mock private UserDtoMapper mapper;
    @Mock private JwtServiceImpl jwtService;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @AfterAll
    static void afterAll() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void canSearch() {
        // When
        underTest.search();

        // Then
        verify(userRepository).findAll();
    }

    @Test
    void canLire() {
        // Given
        Long userId = 1L;
        AppUser user = AppUser.builder()
                .id(1L).username("test").email("test@test.com").enabled(true).build();

        given(userRepository.findById(userId)).willReturn(Optional.ofNullable(user));
        var expected = underTest.lire(userId);

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.getId()).isNotNull();
        assert user != null;
        assertThat(Objects.equals(expected.getId(), user.getId())).isTrue();
        assertThat(Objects.equals(expected.getUsername(), user.getUsername())).isTrue();
    }

    @Test
    void willThrowWhenLire() {
        // Given
        Long userId = 1L;
        String message = "User not found.";
        // Then
        assertThatThrownBy(() -> underTest.lire(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(message);
    }

    @Test
    void canCreateUser() {
        // Given
        UserDto dto = Factory.buildUserDto();
        AppUser user = AppUser.builder().email("test@test.com").username("test").password("password").build();
        when(this.mapper.apply(dto)).thenReturn(user);
        underTest.create(dto);

        // When
        ArgumentCaptor<AppUser> argumentCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(argumentCaptor.capture());

        // Then
        assertThat(argumentCaptor.getValue().getEmail()).isEqualTo(mapper.apply(dto).getEmail());
        assertThat(argumentCaptor.getValue().getUsername()).isEqualTo(mapper.apply(dto).getUsername());
    }

    @Test
    void willThrowWhenUserDtoToSaveIsNull() {
        // Given
        String message = "Unable to save null object";

        // Then
        assertThatThrownBy(() -> underTest.create(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(message);

        verify(userRepository, never()).save(any());
    }

    @Test
    void willThrowWhenUserDtoExistByUsernameOrEmail() {
        // Given
        String username = "test";
        String email = "test@test.com";
        UserDto dto = Factory.buildUserDto();
        AppUser user = AppUser.builder().email("test@test.com").username("test").password("password").build();

        String message = "Duplicate username or email";
        given(userRepository.findByUsernameOrEmail(username, email)).willReturn(Optional.ofNullable(user));

        // Then
        assertThatThrownBy(() -> underTest.create(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(message);
        verify(userRepository, never()).save(any());
    }

    @Test
    void canUpdateUser() {
        // Given
        Long userId = 1L;
        UserDto dto = Factory.buildUserDto();
        AppUser user = AppUser.builder().email("test@test.com").username("test").password("password").build();

        given(this.userRepository.findById(userId)).willReturn(Optional.ofNullable(user));
        underTest.update(userId, dto);

        // When
        ArgumentCaptor<AppUser> argumentCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(argumentCaptor.capture());

        // Then
        assertThat(argumentCaptor.getValue().getEmail()).isEqualTo(dto.email());
        assertThat(argumentCaptor.getValue().getUsername()).isEqualTo(dto.username());
    }

    @Test
    void willThrowWhenUserDtoToUpdateIsNull() {
        // Given
        UserDto dto = Factory.buildUserDto();
        String message = "Unable to update object with null value";
        // Then
        assertThatThrownBy(() -> underTest.update(null, dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(message);

        verify(userRepository, never()).save(any());
    }

    @Test
    void willThrowWhenUsernameOrEmailOfUserDtoToSaveAlreadinExist() {
        // Given
        Long userId = 5L;
        String message = "Duplicate username or email.";
        UserDto dto = Factory.buildUserDto();
        AppUser user1 = AppUser.builder().email("test@test.com").username("test").password("password").build();
        AppUser user2 = AppUser.builder().id(5L).email("test@test.com").username("test").password("password").build();

        // When
        given(this.userRepository.findById(userId)).willReturn(Optional.of(user1));
        given(this.userRepository.findByUsernameOrEmail(user1.getUsername(), user1.getEmail())).willReturn(Optional.of(user2));

        // Then
        assertThatThrownBy(() -> this.underTest.update(userId, dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(message);

        verify(this.userRepository, never()).save(any());
    }

    @Test
    void willThrowWhenValueUserDtoToUpdateIsNull() {
        // Given
        Long userId = 1L;
        String message = "Unable to update object with null value";

        // Then
        assertThatThrownBy(() -> underTest.update(userId, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(message);

        verify(userRepository, never()).save(any());
    }

    @Test
    void willThrowWhenIdForUserToGetTicketIsNull() {
        // Given
        String message = "Unable to retrieve object with null id";

        // Then
        assertThatThrownBy(() -> underTest.userTickets(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(message);

        verify(userRepository, never()).save(any());
    }

    @Test
    void itShouldChectRetrieveAllTicketOfUser() {
        // Given
        Long userId = 1L;
        Ticket ticket1 = Factory.buildTicket();
        Ticket ticket2 = Factory.buildTicket();
        AppUser user = AppUser.builder()
                .id(1L)
                .username("test")
                .email("test@test.com")
                .tickets(List.of(ticket1, ticket2))
                .enabled(true)
                .build();

        given(userRepository.findById(userId)).willReturn(Optional.ofNullable(user));
        var expected = underTest.userTickets(userId);

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.size()).isNotZero();
        assertThat(Objects.equals(expected.size(), 2)).isTrue();
    }


    @Test
    void itShouldChectLogin() {
        // Given
        AuthenticationRequest request = Factory.buildLoginRequest();
        AppUser user = AppUser.builder().email("test@test.com").username("test").password("password").build();

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        var auth = SecurityContextHolder.getContext().getAuthentication();

        when(this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())))
                .thenReturn(auth);
        var claims = new HashMap<String, Object>();
        claims.put("email", user.getEmail());
        when(this.jwtService.generateToken(claims, user)).thenReturn("eyfwjkfbfkfkwfenknkwf.dihfwifbweiefhifhuefuifh.wfuiwhfiuwehfi");


        // When
        var expected =  underTest.authenticate(request);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void willThrowWhenUsernameOrPassworLoginIsNull() {
        // Given
        AuthenticationRequest request = AuthenticationRequest.builder().password("password").build();

        String message = "Username or password not found";
        // When
        assertThatThrownBy(() -> underTest.authenticate(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(message);
    }
}