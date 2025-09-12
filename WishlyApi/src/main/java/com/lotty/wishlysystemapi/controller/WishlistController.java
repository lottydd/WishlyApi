package com.lotty.wishlysystemapi.controller;

import com.lotty.wishlysystemapi.dto.request.item.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistCreateDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistUpdateDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import com.lotty.wishlysystemapi.dto.response.wishlist.*;
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


    //нужна ли тут проверка
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить все вишлисты пользователя")
    @GetMapping("/user/{username}")
    public ResponseEntity<List<WishlistResponseDTO>> getUserWishlists(@PathVariable String username) {
        return ResponseEntity.ok(wishlistService.getUserWishlists(username));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить информацию базовую о вишлисте по ID")
    @GetMapping("/{wishlistid}")
    public ResponseEntity<WishlistResponseDTO> getWishlistInfoById(@PathVariable Integer wishlistid) {
        return ResponseEntity.ok(wishlistService.getWishlistInfo(wishlistid));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить базовую о вишлисте и его список айтемов по ID")
    @GetMapping("/{wishlistid}/item-data")
    public ResponseEntity<WishlistWithItemsResponseDTO> getWishlistInfoWithItemListById(@PathVariable Integer wishlistid) {
        return ResponseEntity.ok(wishlistService.getWishlistInfoWithItemList(wishlistid));
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @Operation(summary = "Получить полную информацию о вишлисте по ID")
    @GetMapping("private-info/{wishlistid}")
    public ResponseEntity<WishlistPrivateInfoDTO> getWishlistPrivateInfoById(@PathVariable Integer wishlistid) {
        return ResponseEntity.ok(wishlistService.getWishlistPrivateInfoById(wishlistid));
    }


    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Обновить информацию о вишлисте")
    @PutMapping("/{wishlistid}")
    public ResponseEntity<WishlistUpdateResponseDTO> updateWishlistData(@RequestBody WishlistUpdateDTO dto) {
        return ResponseEntity.ok(wishlistService.updateWishlist(dto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Добавить существующий айтем в вишлист")
    @PostMapping("/existed-item/{wishlistid}/{itemid}")
    public ResponseEntity<WishlistUpdateResponseDTO> addItemToWishlist(@PathVariable Integer wishlistid,@PathVariable Integer itemid ) {
        return ResponseEntity.ok(wishlistService.addItemToWishlist(wishlistid, itemid));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Получить айтемы из вишлиста по ID")
    @GetMapping("/{wishlistid}/items")
    public ResponseEntity<List<ItemResponseDTO>> getWishlistItemsById(@PathVariable Integer wishlistid) {
        return ResponseEntity.ok(wishlistService.getWishlistItems(wishlistid));
    }


    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Удалить айтем из вишлиста по ID")
    @DeleteMapping("/{wishlistid}/{itemid}")
    public ResponseEntity<WishlistUpdateResponseDTO> removeItemFromWishlistById(@PathVariable Integer wishlistid, @PathVariable Integer itemid) {
        return ResponseEntity.ok(wishlistService.removeItemFromWishlist(wishlistid, itemid));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Создать и добавить айтем в вишлист")
    @PostMapping("/new-item/{wishlistid}")
    public ResponseEntity<WishlistUpdateResponseDTO> createAndAddItemToWishlist(@PathVariable Integer wishlistid,@RequestBody  AddItemToWishlistDTO dto ) {
        return ResponseEntity.ok(wishlistService.createAndAddItemToWishlist(wishlistid, dto));
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @Operation(summary = "Удалить вишлист")
    @DeleteMapping("/{wishlistid}")
    public ResponseEntity<Void> deleteWishlist(@PathVariable Integer wishlistid) {
        wishlistService.deleteWishlist(wishlistid);
        return ResponseEntity.noContent().build();
    }
}
