package com.ennov.tech.services.impls;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.ennov.tech.repositories.UserRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found."));
    }
}
