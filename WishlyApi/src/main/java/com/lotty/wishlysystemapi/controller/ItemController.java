package com.lotty.wishlysystemapi.controller;

import com.lotty.wishlysystemapi.dto.request.item.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.UpdateItemDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import com.lotty.wishlysystemapi.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ItemCreateResponseDTO createItem(@RequestBody AddItemToWishlistDTO dto) {
        return itemService.createItem(dto);
    }

    @Operation(summary = "Обновить айтем")
    @PutMapping
    public ItemResponseDTO updateItem(@RequestBody UpdateItemDTO dto) {
        return itemService.updateItem(dto);
    }

    @Operation(summary = "Удалить айтем")
    @DeleteMapping("/{id}")
    public void deleteItem(
            @Parameter(description = "ID айтема") @PathVariable Integer id) {
        itemService.deleteItem(id);
    }

    @Operation(summary = "Получить айтем по ID")
    @GetMapping("/{id}")
    public ItemResponseDTO getItemById(
            @Parameter(description = "ID айтема") @PathVariable Integer id) {
        return itemService.getItemById(id);
    }

    @Operation(summary = "Получить все айтемы пользователя", description = "⚠ Доступ только к своим айтемам")
    @GetMapping("/user/{userId}")
    public List<ItemResponseDTO> getUserItems(
            @Parameter(description = "ID пользователя") @PathVariable Integer userId) {
        return itemService.getUserItems(userId);
    }
}
