package com.lotty.wishlysystemapi.service;

import com.lotty.wishlysystemapi.entity.WishlistEntity;
import com.lotty.wishlysystemapi.model.Wishlist;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;

    @Autowired
    public WishlistServiceImpl(WishlistRepository wishlistRepository) {
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public Wishlist createWishlist(Wishlist wishlist) {
        WishlistEntity wishlistEntity = new WishlistEntity();
        BeanUtils.copyProperties(wishlist, wishlistEntity);
        WishlistEntity savedWishlistEntity = wishlistRepository.save(wishlistEntity);
        Wishlist savedWishlist = new Wishlist();
        BeanUtils.copyProperties(savedWishlistEntity, savedWishlist);
        return savedWishlist;
    }

    @Override
    public Wishlist updateWishlist(Long id, Wishlist wishlist) {
        WishlistEntity wishlistEntity = wishlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wishlist not found with id: " + id));

        BeanUtils.copyProperties(wishlist, wishlistEntity);
        WishlistEntity updatedWishlistEntity = wishlistRepository.save(wishlistEntity);
        Wishlist updatedWishlist = new Wishlist();
        BeanUtils.copyProperties(updatedWishlistEntity, updatedWishlist);
        return updatedWishlist;
    }

    @Override
    public boolean deleteWishlist(Long id) {
        wishlistRepository.deleteById(id);
        return true;
    }

    @Override
    public Wishlist getWishlistById(Long id) {
        WishlistEntity wishlistEntity = wishlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Wishlist not found with id: " + id));
        Wishlist wishlist = new Wishlist();
        BeanUtils.copyProperties(wishlistEntity, wishlist);
        return wishlist;
    }

    @Override
    public List<Wishlist> getAllWishlists() {
        List<WishlistEntity> wishlistEntities = wishlistRepository.findAll();
        return wishlistEntities.stream()
                .map(wishlistEntity -> {
                    Wishlist wishlist = new Wishlist();
                    BeanUtils.copyProperties(wishlistEntity, wishlist);
                    return wishlist;
                })
                .collect(Collectors.toList());
    }
}