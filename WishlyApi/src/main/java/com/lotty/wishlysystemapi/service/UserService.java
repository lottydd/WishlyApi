package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.dto.request.RequestIdDTO;
import com.lotty.wishlysystemapi.dto.request.user.UserCreateDTO;
import com.lotty.wishlysystemapi.dto.request.user.UserUpdateDTO;
import com.lotty.wishlysystemapi.mapper.UserMapper;
import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.Role;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.repository.RoleDAO;
import com.lotty.wishlysystemapi.repository.UserDAO;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service

public class UserService {


    private final UserDAO userDAO;
    private final UserMapper userMapper;
    private final RoleDAO roleDAO;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(UserDAO userDAO, UserMapper userMapper, RoleDAO roleDAO) {
        this.userDAO = userDAO;
        this.userMapper = userMapper;
        this.roleDAO = roleDAO;
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

    @Transactional
    public User assignRoleToUser(int userId, String roleName) {
        logger.info("Попытка назначения роли пользователю. UserID: {}, Role: {}", userId, roleName);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден. UserID: {}", userId);
                    return new EntityNotFoundException("User not found");
                });

        Role role = roleDAO.findRoleName(roleName)
                .orElseThrow(() -> {
                    logger.error("Роль не найдена. Role: {}", roleName);
                    return new EntityNotFoundException("Role not found " + roleName);
                });

        if (validationRoleDuplication(user, role.getRoleName())) {
            logger.error("Пользователь уже имеет роль. UserID: {}, Role: {}", userId, roleName);
            throw new IllegalArgumentException("Пользователь уже имеет такую роль");
        }
        user.getRoles().add(role);
        userDAO.save(user);
        logger.info("Роль успешно назначена. UserID: {}, Role: {}", userId, roleName);
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public User findUserById(RequestIdDTO dto) {
        logger.info("Поиск пользователя по ID. UserID: {}", dto.getId());

        return userMapper.toDto(userDAO.findById(dto.getId())
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден.   UserID: {}", dto.getId());
                    return new EntityNotFoundException("User not found");
                }));
    }

    @Transactional
    public User deleteRoleFromUser(int userId, String roleName) {
        logger.info("Попытка удаления роли у пользователя. UserID: {}, Role: {}", userId, roleName);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден. UserID: {}", userId);
                    return new EntityNotFoundException("User not found");
                });

        Role role = roleDAO.findRoleName(roleName)
                .orElseThrow(() -> {
                    logger.error("Роль не найдена. Role: {}", roleName);
                    return new EntityNotFoundException("Role not found");
                });

        if (!user.getRoles().contains(role)) {
            logger.error("У пользователя нет такой роли. UserID: {}, Role: {}", userId, roleName);
            throw new IllegalArgumentException("У пользователя нет такой роли");
        }

        user.getRoles().remove(role);
        logger.info("Роль удалена у пользователя. UserID: {}, Role: {}", userId, roleName);

        return userMapper.toDto(user);
    }

    @Transactional
    public void changePassword(int userId, String newPassword) {
        logger.info("Попытка смены пароля пользователя");
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        logger.info("Пользователь с ID {} найден", user.getUserId());
        user.setPassword(passwordEncoder.encode(newPassword));
        userDAO.update(user);
    }

    @Transactional
    public User updateUser(int userId, UserUpdateDTO userUpdateDTO) {
        logger.info("Попытка обновления пользователя. UserID: {}", userId);

        validateUpdateData(userUpdateDTO, userId);

        User user = userDAO.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден. UserID: {}", userId);
                    return new EntityNotFoundException("Пользователь не найден");
                });

        userMapper.updateFromDto(userUpdateDTO, user);

        User updatedUser = userDAO.save(user);

        logger.info("Данные пользователя обновлены. UserID: {}", userId);
        return userMapper.toDto(updatedUser);
    }

    @Transactional(readOnly = true)
    private void validateUpdateData(UserUpdateDTO dto, Integer currentUserId) {
        logger.info("Валидация данных обновления пользователя. UserID: {}", currentUserId);
        if (dto == null) {
            logger.error("Попытка обновления с null данными");
            throw new IllegalArgumentException("Попытка обновления с null данными");
        }

        userDAO.findByEmail(dto.getEmail())
                .filter(user -> !user.getUserId().equals(currentUserId))
                .ifPresent(user -> {
                    logger.error("Попытка обновления на занятый email. Email: {}, CurrentUserID: {}",
                            dto.getEmail(), currentUserId);
                    throw new IllegalArgumentException("Email " + dto.getEmail() + " уже занят другим пользователем");
                });

        userDAO.findByUsername(dto.getUsername())
                .filter(user -> !user.getUserId().equals(currentUserId))
                .ifPresent(user -> {
                    logger.error("Попытка обновления на занятый username. Username: {}, CurrentUserID: {}",
                            dto.getUsername(), currentUserId);
                    throw new IllegalArgumentException("Username " + dto.getUsername() + " уже занят другим пользователем");
                });
        logger.info("Данные обновления валидны");
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

    private boolean validationRoleDuplication(User user, String roleName) {
        return user.getRoles().stream()
                .anyMatch(existingRole -> existingRole.getRoleName().equalsIgnoreCase(roleName));
    }

    @Transactional(readOnly = true)
    public List<Item> getUserItems(Integer userId) {
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return user.getOwnedItems();
    }
}

