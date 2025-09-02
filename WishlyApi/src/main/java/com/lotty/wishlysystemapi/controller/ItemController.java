package com.lotty.wishlysystemapi.controller;

import com.lotty.wishlysystemapi.dto.request.item.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.UpdateItemDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import com.lotty.wishlysystemapi.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
@Tag(name = "Items", description = "Управление айтемами")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @Operation(summary = "Создать новый айтем", description = "Создание айтема и привязка к пользователю")
    @PostMapping
    public ResponseEntity<ItemCreateResponseDTO> createItem(@RequestBody AddItemToWishlistDTO dto) {
        ItemCreateResponseDTO response = itemService.createItem(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Обновить айтем")
    @PutMapping
    public ResponseEntity<ItemResponseDTO> updateItem(@RequestBody UpdateItemDTO dto) {
        ItemResponseDTO response = itemService.updateItem(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Удалить айтем")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(
            @Parameter(description = "ID айтема") @PathVariable Integer id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить айтем по ID")
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> getItemById(
            @Parameter(description = "ID айтема") @PathVariable Integer id) {
        ItemResponseDTO response = itemService.getItemById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получить все айтемы пользователя", description = "⚠ Доступ только к своим айтемам")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ItemResponseDTO>> getUserItems(
            @Parameter(description = "ID пользователя") @PathVariable Integer userId) {
        List<ItemResponseDTO> response = itemService.getUserItems(userId);
        return ResponseEntity.ok(response);
    }
}
