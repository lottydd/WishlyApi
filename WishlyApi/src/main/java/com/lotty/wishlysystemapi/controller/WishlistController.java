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
@RequestMapping("/api/v1/wishlists")
@Tag(name = "Wishlists", description = "Управление вишлистами")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Создать новый вишлист")
    @PostMapping
    public ResponseEntity<WishlistCreateResponseDTO> createWishlist(@RequestBody WishlistCreateDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wishlistService.createWishlist(dto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить все вишлисты пользователя")
    @GetMapping("/user/{username}")
    public ResponseEntity<List<WishlistResponseDTO>> getUserWishlists(@PathVariable String username) {
        return ResponseEntity.ok(wishlistService.getUserWishlists(username));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить вишлист по ID")
    @GetMapping("/{id}")
    public ResponseEntity<WishlistResponseDTO> getWishlistById(@PathVariable Integer id) {
        return ResponseEntity.ok(wishlistService.getWishlistInfo(id));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Обновить вишлист")
    @PutMapping
    public ResponseEntity<WishlistUpdateResponseDTO> updateWishlist(@RequestBody WishlistUpdateDTO dto) {
        return ResponseEntity.ok(wishlistService.updateWishlist(dto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Удалить вишлист")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWishlist(@PathVariable Integer id) {
        wishlistService.deleteWishlist(id);
        return ResponseEntity.noContent().build();
    }
}
