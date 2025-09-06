package com.lotty.wishlysystemapi.controller;

import com.example.common.dto.ParseRequestDTO;
import com.lotty.wishlysystemapi.dto.request.item.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.UpdateItemDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import com.lotty.wishlysystemapi.service.ItemService;
import com.lotty.wishlysystemapi.service.KafkaProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemControllerTest {

    @Mock
    private ItemService itemService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private ItemController itemController;

    private AddItemToWishlistDTO addItemDTO;
    private UpdateItemDTO updateItemDTO;
    private ItemCreateResponseDTO createResponseDTO;
    private ItemResponseDTO itemResponseDTO;

    @BeforeEach
    void setUp() {
        addItemDTO = new AddItemToWishlistDTO();
        addItemDTO.setUserId(1);
        addItemDTO.setWishlistId(101);
        addItemDTO.setItemName("Test Item");
        addItemDTO.setDescription("Test Description");
        addItemDTO.setPrice(99.99);
        addItemDTO.setImageURL("http://test.com/image.jpg");
        addItemDTO.setSourceURL("http://test.com/item");

        updateItemDTO = new UpdateItemDTO();
        updateItemDTO.setItemId(201);
        updateItemDTO.setItemName("Updated Item");
        updateItemDTO.setDescription("Updated Description");
        updateItemDTO.setPrice(199.99);
        updateItemDTO.setImageURL("http://test.com/updated.jpg");
        updateItemDTO.setSourceURL("http://test.com/updated");

        createResponseDTO = new ItemCreateResponseDTO();
        createResponseDTO.setItemId(201);
        createResponseDTO.setItemName("Test Item");
        createResponseDTO.setDescription("Test Description");
        createResponseDTO.setPrice(99.99);
        createResponseDTO.setImageURL("http://test.com/image.jpg");
        createResponseDTO.setSourceURL("http://test.com/item");

        itemResponseDTO = new ItemResponseDTO();
        itemResponseDTO.setItemName("Test Item");
        itemResponseDTO.setDescription("Test Description");
        itemResponseDTO.setPrice(99.99);
        itemResponseDTO.setImageURL("http://test.com/image.jpg");
        itemResponseDTO.setSourceURL("http://test.com/item");
    }

//    @Test
//    void sendParseRequest_ShouldReturnOk() {
//        String url = "http://test.com";
//        Integer userId = 1;
//        Integer wishlistId = 101;
//
//        doNothing().when(kafkaProducerService).sendParseRequest(any(ParseRequestDTO.class));
//
//        ResponseEntity<String> response = itemController.sendParseRequest(url, userId, wishlistId);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Запрос на парсинг отправлен", response.getBody());
//        verify(kafkaProducerService).sendParseRequest(any(ParseRequestDTO.class));
//        }

    @Test
    void createItem_ShouldReturnCreated() {
        when(itemService.createItem(any(AddItemToWishlistDTO.class))).thenReturn(createResponseDTO);

        ResponseEntity<ItemCreateResponseDTO> response = itemController.createItem(addItemDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(201, response.getBody().getItemId());
        verify(itemService).createItem(addItemDTO);
    }

    @Test
    void updateItem_ShouldReturnOk() {
        when(itemService.updateItem(any(UpdateItemDTO.class))).thenReturn(itemResponseDTO);

        ResponseEntity<ItemResponseDTO> response = itemController.updateItem(updateItemDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Item", response.getBody().getItemName());
        verify(itemService).updateItem(updateItemDTO);
    }

    @Test
    void deleteItem_ShouldReturnNoContent() {
        Integer itemId = 201;
        doNothing().when(itemService).deleteItem(itemId);

        ResponseEntity<Void> response = itemController.deleteItem(itemId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
        verify(itemService).deleteItem(itemId);
    }

    @Test
    void getItemById_ShouldReturnOk() {
        Integer itemId = 201;
        when(itemService.getItemById(itemId)).thenReturn(itemResponseDTO);

        ResponseEntity<ItemResponseDTO> response = itemController.getItemById(itemId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Item", response.getBody().getItemName());
        verify(itemService).getItemById(itemId);
    }

    @Test
    void getUserItems_ShouldReturnOk() {
        Integer userId = 1;
        List<ItemResponseDTO> items = List.of(itemResponseDTO);
        when(itemService.getUserItems(userId)).thenReturn(items);

        ResponseEntity<List<ItemResponseDTO>> response = itemController.getUserItems(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        verify(itemService).getUserItems(userId);
    }

    @Test
    void getUserItems_EmptyList_ShouldReturnOk() {
        Integer userId = 1;
        when(itemService.getUserItems(userId)).thenReturn(List.of());

        ResponseEntity<List<ItemResponseDTO>> response = itemController.getUserItems(userId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
        verify(itemService).getUserItems(userId);
    }
}