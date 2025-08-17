package com.lotty.wishlysystemapi.controller;



import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.service.ItemService;
import com.lotty.wishlysystemapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/roles")
@Tag(name = "Roles", description = "Управление ролями пользователей")
public class RoleController {

    private final UserService userService;
    @Autowired
    public RoleController(UserService userService) { this.userService = userService; }

    @PostMapping("/{userId}/admin")
    @Operation(summary = "Назначить роль ADMIN пользователю")
    public ResponseEntity<User> grantAdmin(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.addRole(userId, "ADMIN"));
    }

    @DeleteMapping("/{userId}/admin")
    @Operation(summary = "Снять роль ADMIN у пользователя")
    public ResponseEntity<User> revokeAdmin(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.removeRole(userId, "ADMIN"));
    }
}