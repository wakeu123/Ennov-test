package com.ennov.tech.services.impls;

import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import com.ennov.tech.domains.AppUser;
import com.ennov.tech.domains.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import com.ennov.tech.repositories.UserRepository;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    private UserDetailsServiceImpl underTest;
    @Mock private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        this.underTest = new UserDetailsServiceImpl(this.userRepository);
    }

    @Test
    void itShouldWhenLoadUserByUsername() {
        // Given
        String userName = "test1";
        AppUser user = Factory.buildUser();

        // When
        given(this.userRepository.findByUsername(userName)).willReturn(Optional.of(user));
        var expected = this.underTest.loadUserByUsername(userName);

        // Then
        assertThat(expected).isNotNull();
    }

    @Test
    void willThrowWhenLoadUserByUsername() {
        // Given
        String userName = "test1";
        String message = "Username not found.";

        // Then
        assertThatThrownBy(() -> this.underTest.loadUserByUsername(userName))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining(message);
    }
}