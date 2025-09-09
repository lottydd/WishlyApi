package com.lotty.wishlysystemapi.controller;

import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistCreateDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistUpdateDTO;
import com.lotty.wishlysystemapi.dto.response.wishlist.WishlistCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.wishlist.WishlistResponseDTO;
import com.lotty.wishlysystemapi.dto.response.wishlist.WishlistUpdateResponseDTO;
import com.lotty.wishlysystemapi.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistControllerTest {

    @Mock
    private WishlistService wishlistService;

    @InjectMocks
    private WishlistController wishlistController;

    private WishlistCreateDTO wishlistCreateDTO;
    private WishlistUpdateDTO wishlistUpdateDTO;
    private WishlistCreateResponseDTO createResponseDTO;
    private WishlistResponseDTO wishlistResponseDTO;
    private WishlistUpdateResponseDTO updateResponseDTO;

    @BeforeEach
    void setUp() {
        wishlistCreateDTO = new WishlistCreateDTO();
        wishlistCreateDTO.setWishlistName("Test Wishlist");
        wishlistCreateDTO.setWishlistDescription("Test Description");

        wishlistUpdateDTO = new WishlistUpdateDTO();
        wishlistUpdateDTO.setWishlistId(101);
        wishlistUpdateDTO.setWishlistName("Updated Wishlist");
        wishlistUpdateDTO.setWishlistDescription("Updated Description");

        createResponseDTO = new WishlistCreateResponseDTO();
        createResponseDTO.setWishlistId(101);
        createResponseDTO.setWishlistName("Test Wishlist");
        createResponseDTO.setWishlistDescription("Test Description");
        createResponseDTO.setCreateDate(LocalDateTime.now());

        wishlistResponseDTO = new WishlistResponseDTO();
        wishlistResponseDTO.setUserId(1);
        wishlistResponseDTO.setWishlistId(101);
        wishlistResponseDTO.setWishlistName("Test Wishlist");
        wishlistResponseDTO.setWishlistDescription("Test Description");
        wishlistResponseDTO.setCreateDate(LocalDateTime.now());
        wishlistResponseDTO.setModifiedDate(LocalDateTime.now());
        wishlistResponseDTO.setItemCount(5);

        updateResponseDTO = new WishlistUpdateResponseDTO();
        updateResponseDTO.setWishlistId(101);
        updateResponseDTO.setWishlistName("Updated Wishlist");
        updateResponseDTO.setWishlistDescription("Updated Description");
        updateResponseDTO.setModifiedDate(LocalDateTime.now());
        updateResponseDTO.setItemCount(7);
    }

    @Test
    void createWishlist_ShouldReturnCreated() {
        // Arrange
        when(wishlistService.createWishlist(any(WishlistCreateDTO.class))).thenReturn(createResponseDTO);

        // Act
        ResponseEntity<WishlistCreateResponseDTO> response = wishlistController.createWishlist(wishlistCreateDTO);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(101, response.getBody().getWishlistId());
        verify(wishlistService).createWishlist(wishlistCreateDTO);
    }

    @Test
    void getUserWishlists_ShouldReturnOk() {
        // Arrange
        Integer userId = 1;
        List<WishlistResponseDTO> wishlists = List.of(wishlistResponseDTO);
        when(wishlistService.getUserWishlists(userId)).thenReturn(wishlists);

        // Act
        ResponseEntity<List<WishlistResponseDTO>> response = wishlistController.getUserWishlists(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(101, response.getBody().get(0).getWishlistId());
        verify(wishlistService).getUserWishlists(userId);
    }

    @Test
    void getUserWishlists_EmptyList_ShouldReturnOk() {
        // Arrange
        Integer userId = 1;
        when(wishlistService.getUserWishlists(userId)).thenReturn(List.of());

        // Act
        ResponseEntity<List<WishlistResponseDTO>> response = wishlistController.getUserWishlists(userId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(wishlistService).getUserWishlists(userId);
    }

    @Test
    void getWishlistById_ShouldReturnOk() {
        // Arrange
        Integer wishlistId = 101;
        when(wishlistService.getWishlistById(wishlistId)).thenReturn(wishlistResponseDTO);

        // Act
        ResponseEntity<WishlistResponseDTO> response = wishlistController.getWishlistById(wishlistId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(101, response.getBody().getWishlistId());
        verify(wishlistService).getWishlistById(wishlistId);
    }

    @Test
    void updateWishlist_ShouldReturnOk() {
        // Arrange
        when(wishlistService.updateWishlist(any(WishlistUpdateDTO.class))).thenReturn(updateResponseDTO);

        // Act
        ResponseEntity<WishlistUpdateResponseDTO> response = wishlistController.updateWishlist(wishlistUpdateDTO);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Wishlist", response.getBody().getWishlistName());
        verify(wishlistService).updateWishlist(wishlistUpdateDTO);
    }

    @Test
    void deleteWishlist_ShouldReturnNoContent() {
        // Arrange
        Integer wishlistId = 101;
        doNothing().when(wishlistService).deleteWishlist(wishlistId);

        // Act
        ResponseEntity<Void> response = wishlistController.deleteWishlist(wishlistId);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(wishlistService).deleteWishlist(wishlistId);
    }

    @Test
    void getWishlistById_WithInvalidId_ShouldCallService() {
        // Arrange
        Integer wishlistId = 999;
        when(wishlistService.getWishlistById(wishlistId)).thenReturn(wishlistResponseDTO);

        // Act
        ResponseEntity<WishlistResponseDTO> response = wishlistController.getWishlistById(wishlistId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(wishlistService).getWishlistById(wishlistId);
    }
}