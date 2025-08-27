package com.lotty.wishlysystemapi.service;


import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.repository.UserDAO;
import com.lotty.wishlysystemapi.security.CustomUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(ItemService.class);

    private final UserDAO userDAO;

    public UserDetailsServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Попытка загрузки пользователя по username: {}", username);
        User user = userDAO.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Пользователь с username '{}' не найден", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });

        List<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toList());

        logger.info("Пользователь '{}' найден, количество ролей: {}", username, authorities.size());
        return new CustomUserDetails(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}