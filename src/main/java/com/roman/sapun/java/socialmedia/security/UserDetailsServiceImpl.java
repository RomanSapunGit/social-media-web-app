package com.roman.sapun.java.socialmedia.security;

import com.roman.sapun.java.socialmedia.entity.UserEntity;
import com.roman.sapun.java.socialmedia.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found"));
        if( userEntity.getNotBlocked().equals("false")) {
            throw new DisabledException("User account is blocked");
        }
        return new UserDetailsImpl(userEntity);
    }
}
