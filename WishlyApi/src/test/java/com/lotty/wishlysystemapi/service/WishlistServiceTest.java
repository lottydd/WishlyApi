package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.dto.request.item.AddItemToWishlistDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistCreateDTO;
import com.lotty.wishlysystemapi.dto.request.wishlist.WishlistUpdateDTO;
import com.lotty.wishlysystemapi.dto.response.item.ItemResponseDTO;
import com.lotty.wishlysystemapi.dto.response.wishlist.WishlistCreateResponseDTO;
import com.lotty.wishlysystemapi.dto.response.wishlist.WishlistResponseDTO;
import com.lotty.wishlysystemapi.dto.response.wishlist.WishlistUpdateResponseDTO;
import com.lotty.wishlysystemapi.mapper.ItemMapper;
import com.lotty.wishlysystemapi.mapper.WishlistMapper;
import com.lotty.wishlysystemapi.model.Item;
import com.lotty.wishlysystemapi.model.User;
import com.lotty.wishlysystemapi.model.Wishlist;
import com.lotty.wishlysystemapi.repository.ItemDAO;
import com.lotty.wishlysystemapi.repository.UserDAO;
import com.lotty.wishlysystemapi.repository.WishlistDAO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class WishlistServiceTest {

    @Mock private WishlistMapper wishlistMapper;
    @Mock private UserDAO userDAO;
    @Mock private WishlistDAO wishlistDAO;
    @Mock private ItemDAO itemDAO;
    @Mock private ItemMapper itemMapper;

    @InjectMocks private WishlistService wishlistService;

    private User user;
    private Wishlist wishlist;
    private Item item;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserId(1);

        wishlist = new Wishlist();
        wishlist.setWishlistId(10);
        wishlist.setUser(user);
        wishlist.setCreateDate(LocalDateTime.now());
        wishlist.setModifiedDate(LocalDateTime.now());

        item = new Item();
        item.setItemId(200);
    }

    @Test
    void createAndAddItemToWishlist_success() {
        AddItemToWishlistDTO dto = new AddItemToWishlistDTO();
        when(wishlistDAO.findById(10)).thenReturn(Optional.of(wishlist));
        when(itemMapper.toEntity(dto)).thenReturn(item);
        item.setOwner(user);
        when(itemDAO.save(item)).thenReturn(item);
        when(wishlistDAO.save(wishlist)).thenReturn(wishlist);
        when(wishlistMapper.toWishlistUpdateDTO(wishlist)).thenReturn(new WishlistUpdateResponseDTO());

        WishlistUpdateResponseDTO resp = wishlistService.createAndAddItemToWishlist(10, dto);
        assertThat(resp).isNotNull();
        verify(itemDAO).save(item);
        verify(wishlistDAO).save(wishlist);
    }

    @Test
    void getUserWishlists_returnsList() {
        when(wishlistDAO.findAllByUserId(1)).thenReturn(List.of(wishlist));
        when(wishlistMapper.toWishlistResponseDTOList(List.of(wishlist))).thenReturn(List.of(new WishlistResponseDTO()));
        var out = wishlistService.getUserWishlists(1);
        assertThat(out).hasSize(1);
    }

    @Test
    void updateWishlist_success() {
        WishlistUpdateDTO dto = new WishlistUpdateDTO();
        dto.setWishlistId(10);
        dto.setWishlistName("New name");
        when(wishlistDAO.findById(10)).thenReturn(Optional.of(wishlist));
        wishlist.setWishlistName("New name");
        when(wishlistDAO.save(wishlist)).thenReturn(wishlist);
        when(wishlistMapper.toWishlistUpdateDTO(wishlist)).thenReturn(new WishlistUpdateResponseDTO());
        var resp = wishlistService.updateWishlist(dto);
        assertThat(resp).isNotNull();
    }

    @Test
    void deleteWishlist_success() {
        when(wishlistDAO.existsById(10)).thenReturn(true);
        // delete should not throw
        doNothing().when(wishlistDAO).delete(10);
        wishlistService.deleteWishlist(10);
        verify(wishlistDAO).delete(10);
    }

    @Test
    void deleteWishlist_notFound_throws() {
        when(wishlistDAO.existsById(55)).thenReturn(false);
        assertThatThrownBy(() -> wishlistService.deleteWishlist(55))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void removeItemFromWishlist_success() {
        when(wishlistDAO.findById(10)).thenReturn(Optional.of(wishlist));
        when(itemDAO.findById(200)).thenReturn(Optional.of(item));
        wishlist.getWishlistItems().add(item);
        when(wishlistDAO.save(wishlist)).thenReturn(wishlist);
        when(wishlistMapper.toWishlistUpdateDTO(wishlist)).thenReturn(new WishlistUpdateResponseDTO());

        var resp = wishlistService.removeItemFromWishlist(10, 200);
        assertThat(resp).isNotNull();
        assertThat(wishlist.getWishlistItems()).doesNotContain(item);
    }

    @Test
    void getWishlistItems_success() {
        when(wishlistDAO.findById(10)).thenReturn(Optional.of(wishlist));
        wishlist.getWishlistItems().add(item);
        when(itemMapper.toItemResponseDTOList(wishlist.getWishlistItems())).thenReturn(List.of(new ItemResponseDTO()));
        var list = wishlistService.getWishlistItems(10);
        assertThat(list).hasSize(1);
    }

    @Test
    void getWishlistById_success() {
        when(wishlistDAO.findById(10)).thenReturn(Optional.of(wishlist));
        when(wishlistMapper.toWishlistDTO(wishlist)).thenReturn(new WishlistResponseDTO());
        var resp = wishlistService.getWishlistById(10);
        assertThat(resp).isNotNull();
    }

    @Test
    void getWishlistById_notFound_throws() {
        when(wishlistDAO.findById(999)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> wishlistService.getWishlistById(999))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
