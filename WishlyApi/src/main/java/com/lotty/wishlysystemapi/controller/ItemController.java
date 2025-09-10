package com.lotty.wishlysystemapi.controller;

import com.example.common.dto.ParseRequestDTO;
import com.lotty.wishlysystemapi.dto.request.item.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.UpdateItemDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import com.lotty.wishlysystemapi.dto.response.task.TaskResponseDTO;
import com.lotty.wishlysystemapi.dto.response.task.TaskStatusResponseDTO;
import com.lotty.wishlysystemapi.service.ItemService;
import com.lotty.wishlysystemapi.service.ParsingTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/items")
@Tag(name = "Items", description = "Управление айтемами")
public class ItemController {

    private final ItemService itemService;
    private final ParsingTaskService parsingTaskService;

    public ItemController(ItemService itemService, ParsingTaskService parsingTaskService) {
        this.itemService = itemService;
        this.parsingTaskService = parsingTaskService;
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Создать новый айтем")
    @PostMapping("/create/{username}")
    public ResponseEntity<ItemCreateResponseDTO> createItem(
            @RequestBody AddItemToWishlistDTO dto,
            @PathVariable String username) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(dto, username));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Обновить айтем")
    @PutMapping
    public ResponseEntity<ItemResponseDTO> updateItem(@RequestBody UpdateItemDTO dto) {
        return ResponseEntity.ok(itemService.updateItem(dto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Удалить айтем")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Integer id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить айтем по ID")
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> getItemById(@PathVariable Integer id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить все айтемы пользователя")
    @GetMapping("/user/{username}")
    public ResponseEntity<List<ItemResponseDTO>> getUserItems(@PathVariable String username) {
        return ResponseEntity.ok(itemService.getUserItems(username));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Создать задачу парсинга")
    @PostMapping("/parse")
    public ResponseEntity<TaskResponseDTO> parseAndAddItem(@RequestBody ParseRequestDTO request) {
        return ResponseEntity.accepted().body(parsingTaskService.createAndSendTask(request));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить статус задачи")
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskStatusResponseDTO> getTaskStatus(@PathVariable Integer taskId) {
        return ResponseEntity.ok(parsingTaskService.getTaskStatus(taskId));
    }
}
