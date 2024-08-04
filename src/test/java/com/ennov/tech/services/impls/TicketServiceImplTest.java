package com.ennov.tech.services.impls;

import com.ennov.tech.domains.AppUser;
import com.ennov.tech.domains.Factory;
import com.ennov.tech.domains.Ticket;
import com.ennov.tech.domains.dtos.TicketDto;
import com.ennov.tech.domains.enums.TicketStatut;
import com.ennov.tech.exceptions.BadRequestException;
import com.ennov.tech.exceptions.ConflictException;
import com.ennov.tech.exceptions.EntityNotFoundException;
import com.ennov.tech.repositories.TicketRepository;
import com.ennov.tech.repositories.UserRepository;
import com.ennov.tech.services.mappers.TicketDtoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {

    @InjectMocks
    private TicketServiceImpl underTest;

    @Mock private TicketDtoMapper mapper;
    @Mock private UserRepository userRepository;
    @Mock private TicketRepository ticketRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void canSearch() {
        // When
        this.underTest.search();

        // Then
        verify(this.ticketRepository).findAll();
    }

    @Test
    void canLire() {
        // Given
        Long ticketId = 1L;
        Ticket ticket = Factory.buildTicket();
        ticket.setId(ticketId);

        given(this.ticketRepository.findById(ticketId)).willReturn(Optional.ofNullable(ticket));
        var expected = this.underTest.lire(ticketId);

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.getId()).isNotNull();
        assertThat(Objects.equals(expected.getId(), ticket.getId())).isTrue();
        assertThat(Objects.equals(expected.getTitre(), ticket.getTitre())).isTrue();
    }

    @Test
    void canDelete() {
        //When
        Ticket ticket = Factory.buildTicket();
        ticket.setId(2L);
        given(this.ticketRepository.findById(2L)).willReturn(Optional.of(ticket));
        this.underTest.delete(ticket.getId());

        ArgumentCaptor<Ticket> argumentCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(this.ticketRepository).delete(argumentCaptor.capture());

        // Then
        assertThat(argumentCaptor.getValue().getTitre()).isEqualTo(ticket.getTitre());
        assertThat(argumentCaptor.getValue().getDescription()).isEqualTo(ticket.getDescription());


    }

    @Test
    void willThrowWhenLire() {
        // Given
        String message = "Ticket not found.";
        assertThatThrownBy(() -> this.underTest.lire(null))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(message);
    }

    @Test
    void canCreateTicket() {
        // Given
        TicketDto dto = Factory.buildTicketDto();
        Ticket ticket = Ticket.builder()
                .titre("Titre du ticket de test")
                .description("Description du ticket de test")
                .statut(TicketStatut.IN_PROGRESS)
                .build();

        when(this.mapper.apply(dto)).thenReturn(ticket);

        // When
        this.underTest.create(dto);
        ArgumentCaptor<Ticket> argumentCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(this.ticketRepository).save(argumentCaptor.capture());

        // Then
        assertThat(argumentCaptor.getValue().getTitre()).isEqualTo(dto.titre());
        assertThat(argumentCaptor.getValue().getDescription()).isEqualTo(dto.description());
    }

    @Test
    void willThrowWhenTicketDtoToSaveIsNull() {
        // Given
        String message = "Unable to save null object";

        // Then
        assertThatThrownBy(() -> this.underTest.create(null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(message);

        verify(this.ticketRepository, never()).save(any());
    }

    @Test
    void willThrowWhenTitleOfTicketDtoToSaveAlreadinExist() {
        // Given
        String message = "Duplicate ticket title";
        TicketDto dto = Factory.buildTicketDto();
        Ticket ticket = Ticket.builder()
                .titre("Titre du ticket de test")
                .description("Description du ticket de test")
                .statut(TicketStatut.IN_PROGRESS)
                .build();


        given(this.ticketRepository.findByTitre(dto.titre())).willReturn(Optional.ofNullable(ticket));

        // Then
        assertThatThrownBy(() -> this.underTest.create(dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(message);

        verify(this.ticketRepository, never()).save(any());
    }

    @Test
    void canUpdateTicket() {
        // Given
        Long ticketID = 1L;
        TicketDto dto = Factory.buildTicketDto();
        Ticket ticket = Ticket.builder().titre("Titre du ticket de test").description("Description du ticket de test").statut(TicketStatut.IN_PROGRESS).build();

        when(this.ticketRepository.findById(ticketID)).thenReturn(Optional.ofNullable(ticket));
        this.underTest.update(ticketID, dto);

        // When
        ArgumentCaptor<Ticket> argumentCaptor = ArgumentCaptor.forClass(Ticket.class);
        verify(this.ticketRepository).save(argumentCaptor.capture());

        // Then
        assertThat(argumentCaptor.getValue().getTitre()).isEqualTo(dto.titre());
        assertThat(argumentCaptor.getValue().getDescription()).isEqualTo(dto.description());
    }

    @Test
    void willThrowWhenValueTicketIdToUpdateIsNull() {
        // Given
        TicketDto dto = Factory.buildTicketDto();
        String message = "Unable to update null object";
        // Then
        assertThatThrownBy(() -> underTest.update(null, dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(message);

        verify(userRepository, never()).save(any());
    }

    @Test
    void willThrowWhenValueTicketDtoToUpdateIsNull() {
        // Given
        Long ticketId = 1L;
        Ticket ticket =Factory.buildTicket();
        String message = "Unable to update ticket with null object";
        given(this.ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));

        // Then
        assertThatThrownBy(() -> underTest.update(ticketId, null))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining(message);

        verify(userRepository, never()).save(any());
    }

    @Test
    void willThrowWhenTitleOfTicketDtoToUpdateAlreadinExist() {
        // Given
        Long ticketId = 3L;
        String message = "Duplicate ticket title";
        Ticket ticket = Factory.buildTicket();
        ticket.setId(4L);
        TicketDto dto = Factory.buildTicketDto();

        Ticket ticket2 = Ticket.builder()
                .titre("Titre du ticket de test")
                .description("Description du ticket de test")
                .statut(TicketStatut.IN_PROGRESS)
                .build();

        given(this.ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
        given(this.ticketRepository.findByTitre(dto.titre())).willReturn(Optional.ofNullable(ticket2));

        // Then
        assertThatThrownBy(() -> this.underTest.update(ticketId, dto))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining(message);

        verify(this.ticketRepository, never()).save(any());
    }

    @Test
    void itShouldChectTicketById() {
        // Given
        Long ticketId = 1L;
        Ticket ticket = Factory.buildTicket();
        ticket.setId(1L);

        given(this.ticketRepository.findById(ticketId)).willReturn(Optional.ofNullable(ticket));
        var expected = underTest.lire(ticketId);

        // Then
        assertThat(expected).isNotNull();
        assertThat(Objects.equals(expected.getTitre(), ticket.getTitre())).isTrue();
    }

    @Test
    void itShouldChectWhenRetrieveTicketById() {
        // Given
        Long ticketId = 4L;
        Ticket ticket = Factory.buildTicket();
        ticket.setId(ticketId);

        // When
        given(this.ticketRepository.findById(ticketId)).willReturn(Optional.ofNullable(ticket));
        var expected = this.underTest.getTicketById(ticketId);

        // Then
        assertThat(expected).isNotNull();
        assertThat(Objects.equals(expected.getTitre(), ticket.getTitre())).isTrue();
    }

    @Test
    void itShouldChectWhenAssingTicketToUser() {
        // Given
        Long userId = 3L;
        Long ticketId = 2L;
        AppUser user = Factory.buildUser();
        Ticket ticket = Factory.buildTicket();

        // When
        given(this.ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
        given(this.userRepository.findById(userId)).willReturn(Optional.of(user));

        this.underTest.assignTicketToUser(ticketId, userId);

        // Then
        assertThat(user).isNotNull();
        assertThat(ticket).isNotNull();
        assertThat(ticket.getUser()).isNotNull();
        assertThat(Objects.equals(user.getTickets().size(), 1)).isTrue();
    }

    @Test
    void willThrowWhenUserWhenAssignTicketIsNotFound() {
        // Given
        Long userId = 5L;
        Long ticketId = 3L;
        String message = "User to assign ticket not found.";
        Ticket ticket = Factory.buildTicket();
        ticket.setId(4L);

        given(this.ticketRepository.findById(ticketId)).willReturn(Optional.of(ticket));
        //given(this.userRepository.findById(userId)).willReturn(Optional.ofNullable(this.mapper.apply(dto)));

        // Then
        assertThatThrownBy(() -> this.underTest.assignTicketToUser(ticketId, userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining(message);

        verify(this.userRepository, never()).save(any());
        verify(this.ticketRepository, never()).save(any());
    }
}