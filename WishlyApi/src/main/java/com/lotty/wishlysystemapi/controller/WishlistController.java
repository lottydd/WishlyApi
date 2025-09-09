package com.lotty.wishlysystemapi.controller;

import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistCreateDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistUpdateDTO;
import com.lotty.wishlysystemapi.dto.response.wishlist.WishlistCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.wishlist.WishlistResponseDTO;
import com.lotty.wishlysystemapi.dto.response.wishlist.WishlistUpdateResponseDTO;
import com.lotty.wishlysystemapi.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlists")
@Tag(name = "Wishlists", description = "Управление вишлистами")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @Operation(summary = "Создать новый вишлист")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<WishlistCreateResponseDTO> createWishlist(@RequestBody WishlistCreateDTO dto) {
        WishlistCreateResponseDTO response = wishlistService.createWishlist(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Получить все вишлисты пользователя")
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<WishlistResponseDTO>> getUserWishlists(
            @Parameter(description = "ID пользователя") @PathVariable Integer userId) {
        List<WishlistResponseDTO> response = wishlistService.getUserWishlists(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получить вишлист по ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<WishlistResponseDTO> getWishlistById(
            @Parameter(description = "ID вишлиста") @PathVariable Integer id) {
        WishlistResponseDTO response = wishlistService.getWishlistById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Обновить вишлист")
    @PutMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<WishlistUpdateResponseDTO> updateWishlist(@RequestBody WishlistUpdateDTO dto) {
        WishlistUpdateResponseDTO response = wishlistService.updateWishlist(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Удалить вишлист")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteWishlist(@Parameter(description = "ID вишлиста") @PathVariable Integer id) {
        wishlistService.deleteWishlist(id);
        return ResponseEntity.noContent().build();
    }
}
