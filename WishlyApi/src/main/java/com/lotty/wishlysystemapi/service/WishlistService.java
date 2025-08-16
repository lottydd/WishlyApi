package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.model.Wishlist;

import java.util.List;

public interface WishlistService {
    Wishlist createWishlist(Wishlist wishlist);
    Wishlist updateWishlist(Long id, Wishlist wishlist);
    boolean deleteWishlist(Long id);
    Wishlist getWishlistById(Long id);
    List<Wishlist> getAllWishlists();
}