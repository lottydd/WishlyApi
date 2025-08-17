package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.dto.request.user.UserCreateDTO;
import com.lotty.wishlysystemapi.mapper.UserMapper;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.repository.UserDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

public class UserService {


    private final UserDAO userDAO;
    private final UserMapper userMapper;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserDAO userDAO, UserMapper userMapper) {
        this.userDAO = userDAO;
        this.userMapper = userMapper;
    }


    public User createUser(UserCreateDTO userCreateDTO) {
        logger.info("Попытка регистрации нового пользователя. Email: {}, Username: {}",
                userCreateDTO.getEmail(), userCreateDTO.getUsername());
        validateRegistrationData(userCreateDTO);
        User user = userMapper.toEntity(userCreateDTO);
        user.setPassword(userCreateDTO.getPassword()); //временно
        User savedUser = userDAO.save(user);
        assignRoleToUser(savedUser.getUserId(), "ROLE_USER");
        return userMapper.toDto(savedUser);
    }

    private void assignRoleToUser(Integer userId, String roleUser) {
    }

    public User getUserById(Long id) {
    }

    public List<User> getAllUsers() {
    }

    public User updateEmail(Integer id, String email) {
    }

    public User updateDescription(Integer id, String description) {
    }

    public User updatePassword(Integer id, String password) {
    }

    public User addRole(Integer userId, String admin) {
    }

    public User removeRole(Integer userId, String admin) {
    }


    @Transactional(readOnly = true)
    private void validateRegistrationData(UserCreateDTO dto) {
        logger.info("Валидация данных регистрации пользователя");
        if (dto == null) {
            logger.error("Попытка регистрации с null данными");
            throw new IllegalArgumentException("Попытка регистрации с null данными");
        }

        if (userDAO.existsByEmailOrUsername(dto.getEmail(), dto.getUsername())) {
            logger.error("Попытка регистрации с занятым email или username. Email: {}, Username: {}",
                    dto.getEmail(), dto.getUsername());
            throw new IllegalArgumentException("Email или username уже заняты");
        }
        logger.info("Данные регистрации валидны");
    }

}
