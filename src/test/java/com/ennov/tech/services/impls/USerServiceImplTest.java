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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class USerServiceImplTest {

    @InjectMocks
    private USerServiceImpl underTest;

    private UserDtoMapper mapper;
    private PasswordEncoder passwordEncoder;
    @Mock private UserRepository userRepository;
    @Mock private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        this.mapper = new UserDtoMapper();
        this.passwordEncoder = new BCryptPasswordEncoder();

        underTest = new USerServiceImpl(
                this.mapper,
                new JwtServiceImpl(),
                this.userRepository,
                this.passwordEncoder,
                this.authenticationManager
        );
    }

    @AfterEach
    public void cleanup() {
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
        UserDto dto1 = Factory.buildUserDto();

        String message = "Duplicate username or email";
        given(userRepository.findByUsernameOrEmail(username, email)).willReturn(Optional.ofNullable(mapper.apply(dto1)));

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
        UserDto northerDto = Factory.buildUserDto();

        given(userRepository.findById(userId)).willReturn(Optional.ofNullable(mapper.apply(northerDto)));
        underTest.update(userId, dto);

        // When
        ArgumentCaptor<AppUser> argumentCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(argumentCaptor.capture());

        // Then
        assertThat(argumentCaptor.getValue().getEmail()).isEqualTo(mapper.apply(dto).getEmail());
        assertThat(argumentCaptor.getValue().getUsername()).isEqualTo(mapper.apply(dto).getUsername());
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
        UserDto dto = Factory.buildUserDto();
        AuthenticationRequest request = Factory.buildLoginRequest();


        underTest.create(dto);
        //given(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())))
                //.willReturn(this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())));
        var expected =  underTest.authenticate(request);

        // When
        ArgumentCaptor<AppUser> argumentCaptor = ArgumentCaptor.forClass(AppUser.class);
        verify(userRepository).save(argumentCaptor.capture());

        // Then
        assertThat(argumentCaptor.getValue().getEmail()).isEqualTo(mapper.apply(dto).getEmail());
        assertThat(argumentCaptor.getValue().getUsername()).isEqualTo(mapper.apply(dto).getUsername());
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
//
//    @Test
//    void willThrowWhenOrtherLogin() {
//        // Given
//        AuthenticationRequest request = Factory.buildLoginRequest();
//
//        String message = messageSource.getMessage("unable.to.authenticate.username", new Object[]{ }, locale);
//
//        // When
//        assertThatThrownBy(() -> underTest.authenticate(request))
//                .isInstanceOf(EnnovException.class)
//                .hasMessageContaining(message);
//    }
}