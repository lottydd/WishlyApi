//package com.lotty.wishlysystemapi.service;
//
//import com.lotty.wishlysystemapi.dto.request.item.AddItemToWishlistDTO;
//import com.lotty.wishlysystemapi.dto.request.item.UpdateItemDTO;
//import com.lotty.wishlysystemapi.dto.response.item.ItemCreateResponseDTO;
//import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
//import com.lotty.wishlysystemapi.mapper.ItemMapper;
//import com.lotty.wishlysystemapi.model.Item;
//import com.lotty.wishlysystemapi.model.User;
//import com.lotty.wishlysystemapi.model.Wishlist;
//import com.lotty.wishlysystemapi.repository.ItemDAO;
//import com.lotty.wishlysystemapi.repository.UserDAO;
//import com.lotty.wishlysystemapi.repository.WishlistDAO;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
//class ItemServiceTest {
//
//    @Mock private ItemMapper itemMapper;
//    @Mock private ItemDAO itemDAO;
//    @Mock private UserDAO userDAO;
//    @Mock private WishlistDAO wishlistDAO;
//
//    @InjectMocks private ItemService itemService;
//
//    private User user;
//    private Item item;
//
//    @BeforeEach
//    void setUp() {
//        user = new User();
//        user.setUserId(1);
//        item = new Item();
//        item.setItemId(100);
//        item.setItemName("Test item");
//    }
//
//    @Test
//    void createItem_success() {
//        AddItemToWishlistDTO dto = new AddItemToWishlistDTO();
//        dto.setUserId(1);
//        dto.setItemName("Test");
//
//        when(userDAO.findById(1)).thenReturn(Optional.of(user));
//        when(itemMapper.toEntity(dto)).thenReturn(item);
//        when(itemDAO.save(item)).thenReturn(item);
//        ItemCreateResponseDTO responseDto = new ItemCreateResponseDTO();
//        responseDto.setItemId(100);
//        when(itemMapper.toItemCreateResponseDTO(item)).thenReturn(responseDto);
//
//        ItemCreateResponseDTO result = itemService.createItem(dto);
//
//        assertThat(result).isNotNull();
//        assertThat(result.getItemId()).isEqualTo(100);
//        verify(itemDAO).save(item);
//        verify(userDAO).findById(1);
//    }
//
//    @Test
//    void createItem_userNotFound_throws() {
//        AddItemToWishlistDTO dto = new AddItemToWishlistDTO();
//        dto.setUserId(999);
//
//        when(userDAO.findById(999)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> itemService.createItem(dto))
//                .isInstanceOf(EntityNotFoundException.class);
//        verify(itemDAO, never()).save(any());
//    }
//
//    @Test
//    void updateItem_success() {
//        UpdateItemDTO dto = new UpdateItemDTO();
//        dto.setItemId(100);
//        dto.setItemName("Updated");
//
//        when(itemDAO.findById(100)).thenReturn(Optional.of(item));
//        // update mapper just modifies item; no return
//        doAnswer(invocation -> {
//            UpdateItemDTO passed = invocation.getArgument(0);
//            Item target = invocation.getArgument(1);
//            target.setItemName(passed.getItemName());
//            return null;
//        }).when(itemMapper).updateItemFromDTO(dto, item);
//
//        when(itemDAO.save(item)).thenReturn(item);
//        ItemResponseDTO resp = new ItemResponseDTO();
//        resp.setItemName("Updated");
//        when(itemMapper.toItemResponseDTO(item)).thenReturn(resp);
//
//        ItemResponseDTO result = itemService.updateItem(dto);
//        assertThat(result).isNotNull();
//        assertThat(result.getItemName()).isEqualTo("Updated");
//        verify(itemDAO).save(item);
//    }
//
//    @Test
//    void updateItem_notFound_throws() {
//        UpdateItemDTO dto = new UpdateItemDTO();
//        dto.setItemId(777);
//        when(itemDAO.findById(777)).thenReturn(Optional.empty());
//
//        assertThatThrownBy(() -> itemService.updateItem(dto))
//                .isInstanceOf(EntityNotFoundException.class);
//        verify(itemDAO, never()).save(any());
//    }
//
//    @Test
//    void getUserItems_withItems_returnsList() {
//        when(itemDAO.findAllByOwnerId(1)).thenReturn(List.of(item));
//        ItemResponseDTO resp = new ItemResponseDTO();
//        resp.setItemName(item.getItemName());
//        when(itemMapper.toItemResponseDTOList(List.of(item))).thenReturn(List.of(resp));
//
//        List<ItemResponseDTO> list = itemService.getUserItems(1);
//        assertThat(list).hasSize(1);
//        assertThat(list.get(0).getItemName()).isEqualTo(item.getItemName());
//    }
//
//    @Test
//    void getUserItems_empty_returnsEmptyList() {
//        when(itemDAO.findAllByOwnerId(2)).thenReturn(List.of());
//        when(itemMapper.toItemResponseDTOList(List.of())).thenReturn(List.of());
//
//        List<ItemResponseDTO> list = itemService.getUserItems(2);
//        assertThat(list).isEmpty();
//    }
//
//    @Test
//    void deleteItem_success_removesFromWishlistsAndDeletes() {
//        Item itemToDelete = new Item();
//        itemToDelete.setItemId(200);
//
//        Wishlist w = new Wishlist();
//        w.setWishlistId(10);
//        w.getWishlistItems().add(itemToDelete);
//
//        when(itemDAO.findById(200)).thenReturn(Optional.of(itemToDelete));
//        when(wishlistDAO.findAllByItemId(200)).thenReturn(List.of(w));
//
//        itemService.deleteItem(200);
//
//        // After removal, wishlist saved and itemDAO.delete called
//        verify(wishlistDAO).save(w);
//        verify(itemDAO).delete(200);
//    }
//
//    @Test
//    void deleteItem_notFound_throws() {
//        when(itemDAO.findById(9999)).thenReturn(Optional.empty());
//        assertThatThrownBy(() -> itemService.deleteItem(9999))
//                .isInstanceOf(EntityNotFoundException.class);
//        verify(itemDAO, never()).delete(any());
//    }
//
//    @Test
//    void getItemById_success() {
//        when(itemDAO.findById(100)).thenReturn(Optional.of(item));
//        ItemResponseDTO resp = new ItemResponseDTO();
//        resp.setItemName("Test item");
//        when(itemMapper.toItemResponseDTO(item)).thenReturn(resp);
//
//        ItemResponseDTO result = itemService.getItemById(100);
//        assertThat(result).isNotNull();
//        assertThat(result.getItemName()).isEqualTo("Test item");
//    }
//
//    @Test
//    void getItemById_notFound_throws() {
//        when(itemDAO.findById(555)).thenReturn(Optional.empty());
//        assertThatThrownBy(() -> itemService.getItemById(555))
//                .isInstanceOf(EntityNotFoundException.class);
//    }
//}
