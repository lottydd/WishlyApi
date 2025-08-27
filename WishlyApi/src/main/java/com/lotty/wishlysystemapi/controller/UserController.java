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
            @ApiResponse(responseCode = "200", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные")
    })
    @PostMapping
    public UserCreateResponseDTO createUser(@RequestBody UserCreateDTO dto) {
        return userService.createUser(dto);
    }

    @Operation(summary = "Получить пользователя по ID")
    @GetMapping("/{id}")
    public UserResponseDTO getUserById(
            @Parameter(description = "ID пользователя") @PathVariable Integer id) {
        return userService.findUserById(new RequestIdDTO(id));
    }

    @Operation(summary = "Обновить данные пользователя")
    @PutMapping("/{id}")
    public UserUpdateResponseDTO updateUser(
            @Parameter(description = "ID пользователя") @PathVariable Integer id,
            @RequestBody UserUpdateDTO dto) {
        return userService.updateUser(id, dto);
    }

    @Operation(summary = "Изменить пароль пользователя")
    @PutMapping("/{id}/password")
    public void changePassword(
            @Parameter(description = "ID пользователя") @PathVariable Integer id,
            @RequestParam String newPassword) {
        userService.changePassword(id, newPassword);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Назначить роль пользователю")
    @PostMapping("/{id}/roles/{role}")
    public UserUpdateResponseDTO assignRoleToUser(
            @PathVariable Integer id,
            @PathVariable String role) {
        return userService.assignRoleToUser(id, role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить роль у пользователя")
    @DeleteMapping("/{id}/roles/{role}")
    public UserUpdateResponseDTO deleteRoleFromUser(
            @PathVariable Integer id,
            @PathVariable String role) {
        return userService.deleteRoleFromUser(id, role);
    }
}