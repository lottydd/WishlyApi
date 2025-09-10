package com.lotty.wishlysystemapi.controller;

import com.lotty.wishlysystemapi.dto.request.user.ChangePasswordRequestDTO;
import com.lotty.wishlysystemapi.dto.request.user.UserCreateDTO;
import com.lotty.wishlysystemapi.dto.request.user.UserUpdateDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserPrivateInfoResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserResponseDTO;
import com.lotty.wishlysystemapi.dto.response.user.UserUpdateResponseDTO;
import com.lotty.wishlysystemapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/signup")
    public ResponseEntity<UserCreateResponseDTO> createUser(@RequestBody UserCreateDTO dto) {
        UserCreateResponseDTO response = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Получить информацию о пользователе по ID")
    @GetMapping("/{id}/private-info")    public ResponseEntity<UserPrivateInfoResponseDTO> getUserFullInfo(
            @Parameter(description = "ID пользователя") @PathVariable Integer id) {
        UserPrivateInfoResponseDTO response = userService.getUserByIdForAdmin(id);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить публичную информацию о пользователе по его юзернейму")
    @GetMapping("/{username}/info")
    public ResponseEntity<UserResponseDTO> getUserPublicInfo(
            @Parameter(description = "ID пользователя") @PathVariable String username) {
        UserResponseDTO response = userService.getPublicUserInfo(username);
        return ResponseEntity.ok(response);
    }




    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Обновить данные пользователя")
    @PutMapping("/{username}/update-data")
    public ResponseEntity<UserUpdateResponseDTO> updateUser(
            @Parameter(description = "ID пользователя") @PathVariable String username,
            @RequestBody UserUpdateDTO dto) {
        UserUpdateResponseDTO response = userService.updateUser(username, dto);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Изменить пароль пользователя")
    @PutMapping("/{username}/password")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "ID пользователя") @PathVariable String username,
            @RequestBody ChangePasswordRequestDTO dto) {
        userService.changePassword(username, dto);
        return ResponseEntity.noContent().build();
    }


    @PreAuthorize("hasRole('USER','ADMIN')")
    @Operation(summary = "Получить все айтемы пользователя")
    @PostMapping("{username}/item-list")
    public ResponseEntity<List<ItemResponseDTO>> getUserItems(
            @PathVariable String  username) {
        List<ItemResponseDTO> response = userService.getUserItems(username);
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить роль у пользователя")
    @DeleteMapping("delete-role/{id}/{role}")
    public ResponseEntity<UserUpdateResponseDTO> deleteRoleFromUser(
            @PathVariable Integer id,
            @PathVariable String role) {
        UserUpdateResponseDTO response = userService.deleteRoleFromUser(id, role);
        return ResponseEntity.ok(response);
    }
}
