package com.lotty.wishlysystemapi.controller;

import com.example.common.dto.ParseRequestDTO;
import com.lotty.wishlysystemapi.dto.request.item.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.UpdateItemDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import com.lotty.wishlysystemapi.dto.response.task.TaskResponseDTO;
import com.lotty.wishlysystemapi.dto.response.task.TaskStatusResponseDTO;
import com.lotty.wishlysystemapi.service.ItemService;
import com.lotty.wishlysystemapi.service.KafkaProducerService;
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
@RequestMapping("/api/items")
@Tag(name = "Items", description = "Управление айтемами")
public class ItemController {

    private final ItemService itemService;
    private final KafkaProducerService kafkaProducerService;
    private final ParsingTaskService parsingTaskService;

    public ItemController(ItemService itemService,
                          KafkaProducerService kafkaProducerService,
                          ParsingTaskService parsingTaskService) {
        this.itemService = itemService;
        this.kafkaProducerService = kafkaProducerService;
        this.parsingTaskService = parsingTaskService;
    }

    @Operation(summary = "Создать новый айтем", description = "Создание айтема и привязка к пользователю")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<ItemCreateResponseDTO> createItem(@RequestBody AddItemToWishlistDTO dto) {
        ItemCreateResponseDTO response = itemService.createItem(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Обновить айтем")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping
    public ResponseEntity<ItemResponseDTO> updateItem(@RequestBody UpdateItemDTO dto) {
        ItemResponseDTO response = itemService.updateItem(dto);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Удалить айтем")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@Parameter(description = "ID айтема") @PathVariable Integer id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Получить айтем по ID")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> getItemById(@Parameter(description = "ID айтема") @PathVariable Integer id) {
        ItemResponseDTO response = itemService.getItemById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Получить все айтемы пользователя", description = "⚠ Доступ только к своим айтемам")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ItemResponseDTO>> getUserItems(@Parameter(description = "ID пользователя") @PathVariable Integer userId) {
        List<ItemResponseDTO> response = itemService.getUserItems(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Создать задачу парсинга", description = "Парсинг товара и создание задачи")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PostMapping("/parse")
    public ResponseEntity<TaskResponseDTO> parseAndAddItem(@RequestBody ParseRequestDTO request) {
        TaskResponseDTO response = parsingTaskService.createAndSendTask(request);
        return ResponseEntity.accepted().body(response);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить статус задачи")
    @GetMapping("/tasks/{taskId}")
    public ResponseEntity<TaskStatusResponseDTO> getTaskStatus(@PathVariable Integer taskId) {
        TaskStatusResponseDTO taskStatusResponseDTO = parsingTaskService.getTaskStatus(taskId);
        return ResponseEntity.ok(taskStatusResponseDTO);
    }
}
