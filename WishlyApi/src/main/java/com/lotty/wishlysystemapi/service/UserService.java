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

import java.time.LocalDateTime;
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
        user.setLastPasswordChange(LocalDateTime.now());
        User savedUser = userDAO.save(user);
        assignRoleToUser(savedUser.getUserId(), "ROLE_USER");
        return userMapper.toUserCreateResponseDTO(savedUser);
    }


    @Transactional
    public void assignRoleToUser(int userId, String roleName) {
        logger.info("Попытка назначения роли пользователю. UserID: {}, Role: {}", userId, roleName);

        User user = findUserByIdOrThrow(userId);
        Role role = findRoleByNameOrThrow(roleName);

        validateRoleNotAssigned(user, roleName);
        user.getRoles().add(role);
        userDAO.save(user);

        logger.info("Роль успешно назначена. UserID: {}, Role: {}", userId, roleName);
        userMapper.toUserUpdateResponseDTO(user);
    }



    @Transactional
    public UserUpdateResponseDTO assignRoleToUserByAdmin(int userId, String roleName) {
        logger.info("Попытка назначения роли пользователю. UserID: {}, Role: {}", userId, roleName);

        checkAdminAccess();
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
        checkAdminAccess();
        User user = findUserByIdOrThrow(userId);
        return userMapper.toUserPrivateInfoResponseDTO(user);
    }

    @Transactional
    public UserUpdateResponseDTO deleteRoleFromUser(int userId, String roleName) {
        logger.info("Попытка удаления роли у пользователя. UserID: {}, Role: {}", userId, roleName);

        checkAdminAccess();
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

        User user = findUserByNameOrThrow(requestedUsername);
        validatePasswordStrength(dto.getNewPassword());
        validateNewPasswordNotMatchesOld(dto.getNewPassword(), user);
        validatePasswordChangeAccess(user, dto, currentUsername);

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setLastPasswordChange(LocalDateTime.now());
        userDAO.save(user);

        logger.info("Пароль успешно изменен: инициатор={}, пользователь={}",
                currentUsername, requestedUsername);
    }

    @Transactional
    public UserUpdateResponseDTO updateUser(String username, UserUpdateDTO userUpdateDTO) {
        String currentUsername = SecurityUtils.getCurrentUsername();

        logger.info("Попытка обновления пользователя: {}, инициатор: {}",
                username, currentUsername);

        checkUserAccess(username);
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

        checkUserAccess(username);
        User user = findUserByNameOrThrow(username);
        List<ItemResponseDTO> items = itemMapper.toItemResponseDTOList(user.getOwnedItems());

        if (items.isEmpty()) {
            logger.warn("У пользователя {} нет айтемов", username);
        } else {
            logger.info("У пользователя {} найдено {} айтемов", username, items.size());
        }
        return items;
    }


    @Transactional(readOnly = true)
    public User findUserByNameOrThrow(String username) {
        logger.info("Поиск пользователя по Username: {}", username);
        return userDAO.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username));
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

    private void checkUserAccess(String targetUsername) {
        String currentUsername = SecurityUtils.getCurrentUsername();
        if (!currentUsername.equals(targetUsername) && !SecurityUtils.isAdmin()) {
            logger.error("Попытка несанкционированного доступа: {} -> {}", currentUsername, targetUsername);
            throw new SecurityAuthenticationException("Недостаточно прав");
        }
    }

    private void checkAdminAccess() {
        if (!SecurityUtils.isAdmin()) {
            throw new SecurityAuthenticationException("Требуются права администратора");
        }
    }

    private void validatePasswordChangeAccess(User user, ChangePasswordRequestDTO dto, String currentUsername) {
        if (currentUsername.equals(user.getUsername())) {
            if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
                throw new SecurityAuthenticationException("Неверный текущий пароль");
            }
        } else if (SecurityUtils.isAdmin()) {
            logger.warn("АДМИНИСТРАТИВНОЕ ДЕЙСТВИЕ: {} сбрасывает пароль {}", currentUsername, user.getUsername());
        } else {
            throw new SecurityAuthenticationException("Недостаточно прав для смены пароля");
        }
    }

    private void validateNewPasswordNotMatchesOld(String newPassword, User user) {
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new SecurityAuthenticationException("Новый пароль не должен совпадать со старым");
        }
    }

    private void validateRegistrationData(UserCreateDTO dto) {
        logger.info("Валидация данных регистрации пользователя");

        if (dto == null) {
            throw new IllegalArgumentException("Данные регистрации не могут быть null");
        }

        if (userDAO.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email уже занят");
        }

        if (userDAO.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username уже занят");
        }
    }

    private void validateUpdateData(UserUpdateDTO dto, String username) {
        logger.info("Валидация данных обновления пользователя: {}", username);

        if (dto == null) {
            throw new IllegalArgumentException("Данные обновления не могут быть null");
        }

        userDAO.findByEmail(dto.getEmail())
                .filter(user -> !user.getUsername().equals(username))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Email уже занят другим пользователем");
                });

        userDAO.findByUsername(dto.getUsername())
                .filter(user -> !user.getUsername().equals(username))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Username уже занят другим пользователем");
                });
    }

    private void validateRoleNotAssigned(User user, String roleName) {
        if (user.getRoles().stream()
                .anyMatch(existingRole -> existingRole.getRoleName().equalsIgnoreCase(roleName))) {
            throw new IllegalArgumentException("Пользователь уже имеет такую роль");
        }
    }

    private void validateRoleAssigned(User user, Role role) {
        if (!user.getRoles().contains(role)) {
            throw new IllegalArgumentException("У пользователя нет такой роли");
        }
    }

    private void validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Пароль должен содержать минимум 8 символов");
        }

        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Пароль должен содержать хотя бы одну цифру");
        }

        if (!password.matches(".*[a-z].*") || !password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Пароль должен содержать буквы в верхнем и нижнем регистре");
        }

        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            throw new IllegalArgumentException("Пароль должен содержать хотя бы один специальный символ");
        }
    }
}