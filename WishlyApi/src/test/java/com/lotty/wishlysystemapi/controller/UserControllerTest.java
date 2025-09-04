package com.lotty.wishlysystemapi.controller;

import com.lotty.wishlysystemapi.dto.request.RequestIdDTO;
import com.lotty.wishlysystemapi.dto.request.user.UserCreateDTO;
import com.lotty.wishlysystemapi.dto.request.user.UserUpdateDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserUpdateResponseDTO;
import com.lotty.wishlysystemapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private UserCreateDTO userCreateDTO;
    private UserUpdateDTO userUpdateDTO;
    private UserCreateResponseDTO createResponseDTO;
    private UserResponseDTO userResponseDTO;
    private UserUpdateResponseDTO updateResponseDTO;
    private RequestIdDTO requestIdDTO;

    @BeforeEach
    void setUp() {
        userCreateDTO = new UserCreateDTO();
        userCreateDTO.setUsername("testuser");
        userCreateDTO.setEmail("test@example.com");
        userCreateDTO.setPassword("password");
        userCreateDTO.setDescription("Test user");

        userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setUsername("updateduser");
        userUpdateDTO.setEmail("updated@example.com");
        userUpdateDTO.setDescription("Updated user");

        createResponseDTO = new UserCreateResponseDTO();
        createResponseDTO.setUserId(1);
        createResponseDTO.setUsername("testuser");
        createResponseDTO.setEmail("test@example.com");
        createResponseDTO.setDescription("Test user");
        createResponseDTO.setRoles(List.of("ROLE_USER"));

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1);
        userResponseDTO.setUsername("testuser");
        userResponseDTO.setEmail("test@example.com");
        userResponseDTO.setDescription("Test user");
        userResponseDTO.setRoles(List.of("ROLE_USER"));

        updateResponseDTO = new UserUpdateResponseDTO();
        updateResponseDTO.setUserId(1);
        updateResponseDTO.setUsername("updateduser");
        updateResponseDTO.setEmail("updated@example.com");
        updateResponseDTO.setDescription("Updated user");
        updateResponseDTO.setRoles(List.of("ROLE_USER"));

        requestIdDTO = new RequestIdDTO(1);
    }

    @Test
    void createUser_ShouldReturnCreated() {
        // Arrange
        when(userService.createUser(any(UserCreateDTO.class))).thenReturn(createResponseDTO);

        // Act
        ResponseEntity<UserCreateResponseDTO> response = userController.createUser(userCreateDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getUserId());
        verify(userService).createUser(userCreateDTO);
    }

    @Test
    void getUserById_ShouldReturnOk() {
        // Arrange
        Integer userId = 1;
        when(userService.findUserById(any(RequestIdDTO.class))).thenReturn(userResponseDTO);

        // Act
        ResponseEntity<UserResponseDTO> response = userController.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getId());
        verify(userService).findUserById(argThat(dto -> dto.getId().equals(1)));
    }

    @Test
    void updateUser_ShouldReturnOk() {
        // Arrange
        Integer userId = 1;
        when(userService.updateUser(eq(userId), any(UserUpdateDTO.class))).thenReturn(updateResponseDTO);

        // Act
        ResponseEntity<UserUpdateResponseDTO> response = userController.updateUser(userId, userUpdateDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("updateduser", response.getBody().getUsername());
        verify(userService).updateUser(userId, userUpdateDTO);
    }

    @Test
    void changePassword_ShouldReturnNoContent() {
        // Arrange
        Integer userId = 1;
        String newPassword = "newpassword";
        doNothing().when(userService).changePassword(userId, newPassword);

        // Act
        ResponseEntity<Void> response = userController.changePassword(userId, newPassword);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).changePassword(userId, newPassword);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignRoleToUser_ShouldReturnOk() {
        // Arrange
        Integer userId = 1;
        String role = "ROLE_ADMIN";
        when(userService.assignRoleToUser(userId, role)).thenReturn(updateResponseDTO);

        // Act
        ResponseEntity<UserUpdateResponseDTO> response = userController.assignRoleToUser(userId, role);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userService).assignRoleToUser(userId, role);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteRoleFromUser_ShouldReturnOk() {
        // Arrange
        Integer userId = 1;
        String role = "ROLE_ADMIN";
        when(userService.deleteRoleFromUser(userId, role)).thenReturn(updateResponseDTO);

        // Act
        ResponseEntity<UserUpdateResponseDTO> response = userController.deleteRoleFromUser(userId, role);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(userService).deleteRoleFromUser(userId, role);
    }

    @Test
    void getUserById_WithInvalidId_ShouldCallService() {
        // Arrange
        Integer userId = 999;
        when(userService.findUserById(any(RequestIdDTO.class))).thenReturn(userResponseDTO);

        // Act
        ResponseEntity<UserResponseDTO> response = userController.getUserById(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userService).findUserById(argThat(dto -> dto.getId().equals(999)));
    }
}