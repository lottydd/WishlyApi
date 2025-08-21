CREATE TABLE WishlistItems (
    wishlist_id INT NOT NULL,
    item_id INT NOT NULL,
    PRIMARY KEY(wishlist_id, item_id),
    CONSTRAINT FK_WishlistItems_Wishlist FOREIGN KEY (wishlist_id) REFERENCES Wishlists(wishlistId) ON DELETE CASCADE,
    CONSTRAINT FK_WishlistItems_Item FOREIGN KEY (item_id) REFERENCES Items(itemId) ON DELETE CASCADE
);