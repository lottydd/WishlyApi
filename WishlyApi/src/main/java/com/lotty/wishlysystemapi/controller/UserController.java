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
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Управление пользователями")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Изменить пароль пользователя")
    @PutMapping("/{username}/password")
    public ResponseEntity<Void> changePassword(
            @PathVariable String username,
            @RequestBody ChangePasswordRequestDTO dto) {
        userService.changePassword(username, dto);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Регистрация нового пользователя")
    @PostMapping("/signup")
    public ResponseEntity<UserCreateResponseDTO> createUser(@RequestBody UserCreateDTO dto) {
        UserCreateResponseDTO response = userService.createUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Получить приватную информацию о пользователе по ID")
    @GetMapping("/{userid}/private-info")
    public ResponseEntity<UserPrivateInfoResponseDTO> getUserFullInfo(
            @Parameter(description = "ID пользователя") @PathVariable Integer userid) {
        return ResponseEntity.ok(userService.getUserByIdForAdmin(userid));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить публичную информацию о пользователе по username")
    @GetMapping("/{username}/info")
    public ResponseEntity<UserResponseDTO> getUserPublicInfo(@PathVariable String username) {
        return ResponseEntity.ok(userService.getPublicUserInfo(username));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Обновить данные пользователя")
    @PutMapping("/{username}/update-data")
    public ResponseEntity<UserUpdateResponseDTO> updateUser(
            @PathVariable String username,
            @RequestBody UserUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateUser(username, dto));
    }



    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Получить все айтемы пользователя")
    @GetMapping("/{username}/items")
    public ResponseEntity<List<ItemResponseDTO>> getUserItems(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserItems(username));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Удалить роль у пользователя")
    @DeleteMapping("/{userid}/roles/{role}")
    public ResponseEntity<UserUpdateResponseDTO> deleteRoleFromUser(
            @PathVariable Integer userid,
            @PathVariable String role) {
        return ResponseEntity.ok(userService.deleteRoleFromUser(userid, role));
    }
}
