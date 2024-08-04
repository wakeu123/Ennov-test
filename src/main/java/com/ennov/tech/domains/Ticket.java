package com.ennov.tech.domains;

import com.ennov.tech.domains.enums.TicketStatut;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TICKET")
@Table(name = "ENNOV_TICKET")
public class Ticket implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titre;

    private String description;

    @Enumerated(EnumType.STRING)
    private TicketStatut statut;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private AppUser user;

    @Transient
    private Long userId;

    public Ticket(String titre, String description, TicketStatut status) {
        this.titre = titre;
        this.description = description;
        this.statut = status;
    }

    @PostLoad
    public void init() {
        if(user != null && user.getId() != null) {
            setUserId(user.getId());
        }
    }
}
