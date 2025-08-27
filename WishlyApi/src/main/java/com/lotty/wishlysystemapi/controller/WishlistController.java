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
    @PostMapping
    public WishlistCreateResponseDTO createWishlist(@RequestBody WishlistCreateDTO dto) {
        return wishlistService.createWishlist(dto);
    }

    @Operation(summary = "Получить все вишлисты пользователя")
    @GetMapping("/user/{userId}")
    public List<WishlistResponseDTO> getUserWishlists(
            @Parameter(description = "ID пользователя") @PathVariable Integer userId) {
        return wishlistService.getUserWishlists(userId);
    }

    @Operation(summary = "Получить вишлист по ID")
    @GetMapping("/{id}")
    public WishlistResponseDTO getWishlistById(
            @Parameter(description = "ID вишлиста") @PathVariable Integer id) {
        return wishlistService.getWishlistById(id);
    }

    @Operation(summary = "Обновить вишлист")
    @PutMapping
    public WishlistUpdateResponseDTO updateWishlist(@RequestBody WishlistUpdateDTO dto) {
        return wishlistService.updateWishlist(dto);
    }

    @Operation(summary = "Удалить вишлист")
    @DeleteMapping("/{id}")
    public void deleteWishlist(@Parameter(description = "ID вишлиста") @PathVariable Integer id) {
        wishlistService.deleteWishlist(id);
    }
}