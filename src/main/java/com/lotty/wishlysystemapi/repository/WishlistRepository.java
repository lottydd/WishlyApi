package com.lotty.wishlysystemapi.repository;

import com.lotty.wishlysystemapi.entity.WishlistEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistRepository extends JpaRepository<WishlistEntity, Long> {
}