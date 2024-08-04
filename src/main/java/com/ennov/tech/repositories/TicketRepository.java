package com.ennov.tech.repositories;

import com.ennov.tech.domains.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByTitre(String titre);
}
