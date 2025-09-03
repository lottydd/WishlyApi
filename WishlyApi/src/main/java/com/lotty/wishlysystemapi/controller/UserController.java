package com.lotty.wishlysystemapi.controller;

import com.lotty.wishlysystemapi.dto.request.RequestIdDTO;
import com.lotty.wishlysystemapi.dto.request.user.UserCreateDTO;
import com.lotty.wishlysystemapi.dto.request.user.UserUpdateDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserUpdateResponseDTO;
import com.lotty.wishlysystemapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Управление пользователями")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Создать нового пользователя", description = "Регистрация нового пользователя в системе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping
    public ResponseEntity<UserCreateResponseDTO> createUser(@RequestBody UserCreateDTO dto) {
        UserCreateResponseDTO response = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Получить пользователя по ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(
            @Parameter(description = "ID пользователя") @PathVariable Integer id) {
        UserResponseDTO response = userService.findUserById(new RequestIdDTO(id));
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Обновить данные пользователя")
    @PutMapping("/{id}")
    public ResponseEntity<UserUpdateResponseDTO> updateUser(
            @Parameter(description = "ID пользователя") @PathVariable Integer id,
            @RequestBody UserUpdateDTO dto) {
        UserUpdateResponseDTO response = userService.updateUser(id, dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Изменить пароль пользователя")
    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "ID пользователя") @PathVariable Integer id,
            @RequestParam String newPassword) {
        userService.changePassword(id, newPassword);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Назначить роль пользователю")
    @PostMapping("/{id}/roles/{role}")
    public ResponseEntity<UserUpdateResponseDTO> assignRoleToUser(
            @PathVariable Integer id,
            @PathVariable String role) {
        UserUpdateResponseDTO response = userService.assignRoleToUser(id, role);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить роль у пользователя")
    @DeleteMapping("/{id}/roles/{role}")
    public ResponseEntity<UserUpdateResponseDTO> deleteRoleFromUser(
            @PathVariable Integer id,
            @PathVariable String role) {
        UserUpdateResponseDTO response = userService.deleteRoleFromUser(id, role);
        return ResponseEntity.ok(response);
    }
}
