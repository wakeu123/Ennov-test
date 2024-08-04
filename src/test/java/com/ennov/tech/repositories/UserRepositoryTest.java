package com.ennov.tech.repositories;

import com.ennov.tech.domains.AppUser;
import com.ennov.tech.domains.Factory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired private UserRepository underTest;

    @Test
    void itShouldCheckIfExistUserByUsername() {
        // Given
        underTest.save(Factory.buildUser());

        // When
        AppUser expected = underTest.findByUsername(Factory.buildUser().getUsername()).get();

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.getUsername()).isEqualTo(Factory.buildUser().getUsername());
    }

    @Test
    void itShouldCheckIfExistUserByUsernameOrEmail() {
        // Given
        underTest.save(Factory.buildUser());

        // When
        AppUser expected = underTest.findByUsernameOrEmail(Factory.buildUser().getUsername(), Factory.buildUser().getEmail()).get();

        // Then
        assertThat(expected).isNotNull();
        assertThat(expected.getEmail()).isEqualTo(Factory.buildUser().getEmail());
        assertThat(expected.getUsername()).isEqualTo(Factory.buildUser().getUsername());
    }

}