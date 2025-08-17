package com.lotty.wishlysystemapi.controller;


import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.model.Wishlist;
import com.lotty.wishlysystemapi.service.ItemListService;
import com.lotty.wishlysystemapi.service.ItemService;
import com.lotty.wishlysystemapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/itemlist")
@Tag(name = "ItemList", description = "Операции с глобальным списком предметов пользователя")
public class ItemListController {

    private final ItemListService itemListService;
    @Autowired
    public ItemListController(ItemListService itemListService) { this.itemListService = itemListService; }

    @PostMapping("/{userId}/items/{itemId}/wishlist/{wishlistId}")
    @Operation(summary = "Добавить предмет из ItemList в вишлист")
    public ResponseEntity<Wishlist> addItemToWishlist(@PathVariable Integer userId,
                                                      @PathVariable Integer itemId,
                                                      @PathVariable Integer wishlistId) {
        return ResponseEntity.ok(itemListService.addItemToWishlist(userId, itemId, wishlistId));
    }

    @DeleteMapping("/{userId}/items/{itemId}")
    @Operation(summary = "Удалить предмет из ItemList (и из всех вишлистов)")
    public ResponseEntity<Void> removeItem(@PathVariable Integer userId, @PathVariable Integer itemId) {
        itemListService.removeItem(userId, itemId);
        return ResponseEntity.noContent().build();
    }
}