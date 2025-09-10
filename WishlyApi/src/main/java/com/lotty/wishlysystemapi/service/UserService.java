package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.dto.request.user.ChangePasswordRequestDTO;
import com.lotty.wishlysystemapi.dto.request.user.UserCreateDTO;
import com.lotty.wishlysystemapi.dto.request.user.UserUpdateDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserPrivateInfoResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserUpdateResponseDTO;
import com.lotty.wishlysystemapi.exception.SecurityAuthenticationException;
import com.lotty.wishlysystemapi.mapper.ItemMapper;
import com.lotty.wishlysystemapi.mapper.UserMapper;
import com.lotty.wishlysystemapi.model.Role;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.repository.RoleDAO;
import com.lotty.wishlysystemapi.repository.UserDAO;
import com.lotty.wishlysystemapi.security.SecurityUtils;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
        User user = userMapper.toEntity(userCreateDTO, passwordEncoder);

        User savedUser = userDAO.save(user);
        assignRoleToUser(savedUser.getUserId(), "ROLE_USER");
        return userMapper.toUserCreateResponseDTO(savedUser);
    }

    @Transactional
    public UserUpdateResponseDTO assignRoleToUser(int userId, String roleName) {
        logger.info("Попытка назначения роли пользователю. UserID: {}, Role: {}", userId, roleName);
        if (!SecurityUtils.isAdmin()) {
            logger.error("Попытка назначения роли без прав администратора. Текущий пользователь: {}",
                    SecurityUtils.getCurrentUsername());
            throw new SecurityException("Недостаточно прав для назначения ролей");
        }
        User user = findUserByIdOrThrow(userId);
        Role role = findRoleByNameOrThrow(roleName);
        validateRoleNotAssigned(user, roleName);
        user.getRoles().add(role);
        userDAO.save(user);

        logger.info("Роль успешно назначена. UserID: {}, Role: {}", userId, roleName);
        return userMapper.toUserUpdateResponseDTO(user);
    }


    @Transactional(readOnly = true)
    public UserResponseDTO getPublicUserInfo(String username) {
        User user = findUserByNameOrThrow(username);
        return userMapper.toUserResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public UserPrivateInfoResponseDTO getUserByIdForAdmin(Integer userId) {
        if (!SecurityUtils.isAdmin()) {
            throw new SecurityException("Требуются права администратора");
        }
        User user = findUserByIdOrThrow(userId);
        return userMapper.toUserPrivateInfoResponseDTO(user);
    }


    @Transactional
    public UserUpdateResponseDTO deleteRoleFromUser(int userId, String roleName) {
        logger.info("Попытка удаления роли у пользователя. UserID: {}, Role: {}", userId, roleName);
        if (!SecurityUtils.isAdmin()) {
            logger.error("Попытка удаления роли без прав администратора. Текущий пользователь: {}",
                    SecurityUtils.getCurrentUsername());
            throw new SecurityException("Недостаточно прав для удаления ролей");
        }

        User user = findUserByIdOrThrow(userId);
        Role role = findRoleByNameOrThrow(roleName);
        validateRoleAssigned(user, role);
        user.getRoles().remove(role);
        userDAO.save(user);

        logger.info("Роль удалена у пользователя. UserID: {}, Role: {}", userId, roleName);
        return userMapper.toUserUpdateResponseDTO(user);
    }

    @Transactional
    public void changePassword(String requestedUsername, ChangePasswordRequestDTO dto) {
        String currentUsername = SecurityUtils.getCurrentUsername();

        logger.info("Попытка смены пароля: инициатор={}, запрошенный пользователь={}",
                currentUsername, requestedUsername);

        validatePasswordStrength(dto.getNewPassword());
        User user = findUserByNameOrThrow(requestedUsername);

        // 1. Проверяем что новый пароль не совпадает со старым
        if (passwordEncoder.matches(dto.getNewPassword(), user.getPassword())) {
            throw new SecurityAuthenticationException("Новый пароль не должен совпадать со старым");
        }

        // 2. Проверяем права доступа - КРИТИЧЕСКИ ВАЖНОЕ ИСПРАВЛЕНИЕ
        if (currentUsername.equals(requestedUsername)) {
            // Смена собственного пароля - проверяем старый пароль
            if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
                throw new SecurityAuthenticationException("Неверный текущий пароль");
            }
        } else if (SecurityUtils.isAdmin()) {
            // Админ сбрасывает пароль другому пользователю
            logger.warn("АДМИНИСТРАТИВНОЕ ДЕЙСТВИЕ: {} сбрасывает пароль пользователя {}",
                    currentUsername, requestedUsername);
        } else {
            // Несанкционированный доступ
            logger.error("ПОПЫТКА НЕСАНКЦИОНИРОВАННОГО ДОСТУПА: {} пытается сменить пароль {}",
                    currentUsername, requestedUsername);
            throw new SecurityAuthenticationException("Недостаточно прав для смены пароля");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userDAO.save(user);

        logger.info("Пароль успешно изменен: инициатор={}, пользователь={}",
                currentUsername, requestedUsername);
    }


    @Transactional
    public UserUpdateResponseDTO updateUser(String username, UserUpdateDTO userUpdateDTO) {
        String currentUsername = SecurityUtils.getCurrentUsername();

        logger.info("Попытка обновления пользователя: {}, инициатор: {}",
                username, currentUsername);

        if (!currentUsername.equals(username) &&  !SecurityUtils.isAdmin()) {
            logger.error("ПОПЫТКА НЕСАНКЦИОНИРОВАННОГО ДОСТУПА: {} пытается изменить данные {}",
                    currentUsername, username);
            throw new SecurityAuthenticationException("Недостаточно прав для обновления данных");
        }

        validateUpdateData(userUpdateDTO, username);
        User user = findUserByNameOrThrow(username);
        userMapper.updateUserFromDto(userUpdateDTO, user);
        User updatedUser = userDAO.save(user);

        if (currentUsername.equals(username)) {
            logger.info("Пользователь {} обновил свои данные", username);
        } else {
            logger.warn("АДМИНИСТРАТИВНОЕ ДЕЙСТВИЕ: {} обновил данные пользователя {}",
                    currentUsername, username);
        }

        return userMapper.toUserUpdateResponseDTO(updatedUser);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> getUserItems(String username) {

        String currentUsername = SecurityUtils.getCurrentUsername();

        logger.info("Получение списка айтемов для пользователя: {}, инициатор: {}",
                username, currentUsername);
        if (!currentUsername.equals(username) &&  !SecurityUtils.isAdmin()) {
            logger.error("ПОПЫТКА НЕСАНКЦИОНИРОВАННОГО ДОСТУПА: {} пытается получить данные {}",
                    currentUsername, username);
            throw new SecurityAuthenticationException("Недостаточно прав для получения данных");
        }
        User user = findUserByNameOrThrow(username);
        List<ItemResponseDTO> items = itemMapper.toItemResponseDTOList(user.getOwnedItems());

        if (items.isEmpty()) {
            logger.warn("У пользователя ID {} нет айтемов", username);
        } else {
            logger.info("У пользователя ID {} найдено {} айтемов", username, items.size());
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

    private void validateUpdateData(UserUpdateDTO dto, String username) {
        logger.info("Валидация данных обновления пользователя. UserID: {}", username);

        if (dto == null) {
            logger.error("Попытка обновления с null данными");
            throw new IllegalArgumentException("Данные обновления не могут быть null");
        }

        userDAO.findByEmail(dto.getEmail())
                .filter(user -> !user.getUsername().equals(username))
                .ifPresent(user -> {
                    logger.error("Email уже занят другим пользователем. Email: {}", dto.getEmail());
                    throw new IllegalArgumentException("Email уже занят другим пользователем");
                });

        userDAO.findByUsername(dto.getUsername())
                .filter(user -> !user.getUsername().equals(username))
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


    @Transactional(readOnly = true)
    public User findUserByIdForTask(Integer userId) {
        logger.info("Поиск пользователя по  ID. UserID: {}", userId);
        return findUserByIdOrThrow(userId);

    }

    @Transactional(readOnly = true)
    public User findUserByNameOrThrow(String username) {
        logger.info("Поиск пользователя по Username. Username: {}", username);

        return userDAO.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
    }

    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Пароль должен содержать минимум 8 символов");
        }

        // Проверка на наличие цифр
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Пароль должен содержать хотя бы одну цифру");
        }

        // Проверка на наличие букв в верхнем и нижнем регистре
        if (!password.matches(".*[a-z].*") || !password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Пароль должен содержать буквы в верхнем и нижнем регистре");
        }

        // Проверка на специальные символы
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            throw new IllegalArgumentException("Пароль должен содержать хотя бы один специальный символ");
        }
    }


}