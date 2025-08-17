package com.lotty.wishlysystemapi.controller;

import com.lotty.wishlysystemapi.dto.request.user.UserCreateDTO;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/users")
@Tag(name = "Users", description = "Операции с пользователями")
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) { this.userService = userService; }

    @PostMapping
    @Operation(summary = "Создать нового пользователя")
    public ResponseEntity<User> createUser(@RequestBody UserCreateDTO dto) {
        return ResponseEntity.ok(userService.createUser(dto));
    }

    @PatchMapping("/{id}/email")
    @Operation(summary = "Изменить email пользователя")
    public ResponseEntity<User> updateEmail(@PathVariable Integer id, @RequestParam String email) {
        return ResponseEntity.ok(userService.updateEmail(id, email));
    }

    @PatchMapping("/{id}/description")
    @Operation(summary = "Изменить описание пользователя")
    public ResponseEntity<User> updateDescription(@PathVariable Integer id, @RequestParam String description) {
        return ResponseEntity.ok(userService.updateDescription(id, description));
    }

    @PatchMapping("/{id}/password")
    @Operation(summary = "Изменить пароль пользователя")
    public ResponseEntity<User> updatePassword(@PathVariable Integer id, @RequestParam String password) {
        return ResponseEntity.ok(userService.updatePassword(id, password));
    }
}