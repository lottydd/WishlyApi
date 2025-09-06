package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.dto.request.RequestIdDTO;
import com.lotty.wishlysystemapi.dto.request.user.UserCreateDTO;
import com.lotty.wishlysystemapi.dto.request.user.UserUpdateDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserUpdateResponseDTO;
import com.lotty.wishlysystemapi.mapper.ItemMapper;
import com.lotty.wishlysystemapi.mapper.UserMapper;
import com.lotty.wishlysystemapi.model.Role;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.repository.RoleDAO;
import com.lotty.wishlysystemapi.repository.UserDAO;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserDAO userDAO;
    private final UserMapper userMapper;
    private final RoleDAO roleDAO;
    private final ItemMapper itemMapper;
    private final PasswordEncoder passwordEncoder;


    @Autowired

    public UserService(UserDAO userDAO, UserMapper userMapper, RoleDAO roleDAO,
                       ItemMapper itemMapper, PasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.userMapper = userMapper;
        this.roleDAO = roleDAO;
        this.itemMapper = itemMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserCreateResponseDTO createUser(UserCreateDTO userCreateDTO) {
        logger.info("Попытка регистрации нового пользователя. Email: {}, Username: {}",
                userCreateDTO.getEmail(), userCreateDTO.getUsername());

        validateRegistrationData(userCreateDTO);

        // Передаём PasswordEncoder в маппер через @Context
        User user = userMapper.toEntity(userCreateDTO, passwordEncoder);

        User savedUser = userDAO.save(user);
        assignRoleToUser(savedUser.getUserId(), "ROLE_USER");
        return userMapper.toUserCreateResponseDTO(savedUser);
    }

    @Transactional
    public UserUpdateResponseDTO assignRoleToUser(int userId, String roleName) {
        logger.info("Попытка назначения роли пользователю. UserID: {}, Role: {}", userId, roleName);

        User user = findUserByIdOrThrow(userId);
        Role role = findRoleByNameOrThrow(roleName);

        validateRoleNotAssigned(user, roleName);

        user.getRoles().add(role);
        userDAO.save(user);

        logger.info("Роль успешно назначена. UserID: {}, Role: {}", userId, roleName);
        return userMapper.toUserUpdateResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO findUserById(RequestIdDTO dto) {
        logger.info("Поиск пользователя по ID. UserID: {}", dto.getId());

        User user = findUserByIdOrThrow(dto.getId());
        return userMapper.toUserResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public User findUserByIdForTask(Integer userId) {
        logger.info("Поиск пользователя по ID. UserID: {}", userId);
        return  findUserByIdOrThrow(userId);

    }


    @Transactional
    public UserUpdateResponseDTO deleteRoleFromUser(int userId, String roleName) {
        logger.info("Попытка удаления роли у пользователя. UserID: {}, Role: {}", userId, roleName);

        User user = findUserByIdOrThrow(userId);
        Role role = findRoleByNameOrThrow(roleName);

        validateRoleAssigned(user, role);

        user.getRoles().remove(role);
        userDAO.save(user);

        logger.info("Роль удалена у пользователя. UserID: {}, Role: {}", userId, roleName);
        return userMapper.toUserUpdateResponseDTO(user);
    }

    @Transactional
    public void changePassword(int userId, String newPassword) {
        logger.info("Попытка смены пароля для пользователя ID: {}", userId);
        User user = findUserByIdOrThrow(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userDAO.save(user);
        logger.info("Пароль успешно изменен для пользователя ID: {}", userId);
    }

    @Transactional
    public UserUpdateResponseDTO updateUser(int userId, UserUpdateDTO userUpdateDTO) {
        logger.info("Попытка обновления пользователя. UserID: {}", userId);

        validateUpdateData(userUpdateDTO, userId);

        User user = findUserByIdOrThrow(userId);
        userMapper.updateUserFromDto(userUpdateDTO, user);

        User updatedUser = userDAO.save(user);

        logger.info("Данные пользователя обновлены. UserID: {}", userId);
        return userMapper.toUserUpdateResponseDTO(updatedUser);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> getUserItems(Integer userId) {
        logger.info("Получение списка айтемов для пользователя ID: {}", userId);
        User user = findUserByIdOrThrow(userId);
        List<ItemResponseDTO> items = itemMapper.toItemResponseDTOList(user.getOwnedItems());
        if (items.isEmpty()) {
            logger.warn("У пользователя ID {} нет айтемов", userId);
        } else {
            logger.info("У пользователя ID {} найдено {} айтемов", userId, items.size());
        }
        return items;
    }

    private User findUserByIdOrThrow(int userId) {
        return userDAO.findById(userId)
                .orElseThrow(() -> {
                    logger.error("Пользователь не найден. UserID: {}", userId);
                    return new EntityNotFoundException("User not found with id: " + userId);
                });
    }

    private Role findRoleByNameOrThrow(String roleName) {
        return roleDAO.findRoleName(roleName)
                .orElseThrow(() -> {
                    logger.error("Роль не найдена. Role: {}", roleName);
                    return new EntityNotFoundException("Role not found: " + roleName);
                });
    }

    private void validateRegistrationData(UserCreateDTO dto) {
        logger.info("Валидация данных регистрации пользователя");

        if (dto == null) {
            logger.error("Попытка регистрации с null данными");
            throw new IllegalArgumentException("Данные регистрации не могут быть null");
        }

        if (userDAO.existsByEmail(dto.getEmail())) {
            logger.error("Email уже занят. Email: {}", dto.getEmail());
            throw new IllegalArgumentException("Email уже занят");
        }

        if (userDAO.existsByUsername(dto.getUsername())) {
            logger.error("Username уже занят. Username: {}", dto.getUsername());
            throw new IllegalArgumentException("Username уже занят");
        }

        logger.info("Данные регистрации валидны");
    }

    private void validateUpdateData(UserUpdateDTO dto, Integer currentUserId) {
        logger.info("Валидация данных обновления пользователя. UserID: {}", currentUserId);

        if (dto == null) {
            logger.error("Попытка обновления с null данными");
            throw new IllegalArgumentException("Данные обновления не могут быть null");
        }

        userDAO.findByEmail(dto.getEmail())
                .filter(user -> !user.getUserId().equals(currentUserId))
                .ifPresent(user -> {
                    logger.error("Email уже занят другим пользователем. Email: {}", dto.getEmail());
                    throw new IllegalArgumentException("Email уже занят другим пользователем");
                });

        userDAO.findByUsername(dto.getUsername())
                .filter(user -> !user.getUserId().equals(currentUserId))
                .ifPresent(user -> {
                    logger.error("Username уже занят другим пользователем. Username: {}", dto.getUsername());
                    throw new IllegalArgumentException("Username уже занят другим пользователем");
                });

        logger.info("Данные обновления валидны");
    }

    private void validateRoleNotAssigned(User user, String roleName) {
        if (user.getRoles().stream()
                .anyMatch(existingRole -> existingRole.getRoleName().equalsIgnoreCase(roleName))) {
            logger.error("Пользователь уже имеет роль. UserID: {}, Role: {}", user.getUserId(), roleName);
            throw new IllegalArgumentException("Пользователь уже имеет такую роль");
        }
    }

    private void validateRoleAssigned(User user, Role role) {
        if (!user.getRoles().contains(role)) {
            logger.error("У пользователя нет такой роли. UserID: {}, Role: {}", user.getUserId(), role.getRoleName());
            throw new IllegalArgumentException("У пользователя нет такой роли");
        }
    }
}