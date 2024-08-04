package com.ennov.tech.repositories;


import com.ennov.tech.domains.Factory;
import com.ennov.tech.domains.Ticket;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class TicketRepositoryTest {

    @Autowired private TicketRepository underTest;

    @Test
    void itShouldCheckIfExistTicket() {
        // Given
        underTest.save(Factory.buildTicket());

        // When
        Optional<Ticket> expected = underTest.findByTitre("Titre du ticket");

        // Then
        assertThat(expected.get()).isNotNull();
        assertThat(expected.get().getTitre()).isEqualTo(Factory.buildTicket().getTitre());
    }

}