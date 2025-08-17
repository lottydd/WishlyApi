package com.lotty.wishlysystemapi.controller;

import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistCreateDTO;
import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.Wishlist;
import com.lotty.wishlysystemapi.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlists")
@Tag(name = "Wishlists", description = "Операции со списками желаемого")
public class WishlistController {

    private final WishlistService wishlistService;
    @Autowired
    public WishlistController(WishlistService wishlistService) { this.wishlistService = wishlistService; }

    @PostMapping
    @Operation(summary = "Создать новый вишлист")
    public ResponseEntity<Wishlist> createWishlist(@RequestBody WishlistCreateDTO dto) {
        return ResponseEntity.ok(wishlistService.createWishlist(dto));
    }

    @PatchMapping("/{id}/description")
    @Operation(summary = "Изменить описание вишлиста")
    public ResponseEntity<Wishlist> updateDescription(@PathVariable Integer id, @RequestParam String description) {
        return ResponseEntity.ok(wishlistService.updateDescription(id, description));
    }

    @GetMapping("/{id}/items")
    @Operation(summary = "Получить список предметов в вишлисте")
    public ResponseEntity<List<Item>> getItems(@PathVariable Integer id) {
        return ResponseEntity.ok(wishlistService.getItems(id));
    }

    @PostMapping("/{id}/items/{itemId}")
    @Operation(summary = "Добавить предмет в вишлист")
    public ResponseEntity<Wishlist> addItem(@PathVariable Integer id, @PathVariable Integer itemId) {
        return ResponseEntity.ok(wishlistService.addItem(id, itemId));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @Operation(summary = "Удалить предмет из вишлиста")
    public ResponseEntity<Wishlist> removeItem(@PathVariable Integer id, @PathVariable Integer itemId) {
        return ResponseEntity.ok(wishlistService.removeItem(id, itemId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить вишлист")
    public ResponseEntity<Void> deleteWishlist(@PathVariable Integer id) {
        wishlistService.deleteWishlist(id);
        return ResponseEntity.noContent().build();
    }
}