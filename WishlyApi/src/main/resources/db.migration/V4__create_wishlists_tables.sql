CREATE TABLE Wishlists (
    wishlistId INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    wishlistName VARCHAR(255) NOT NULL,
    wishlistDescription VARCHAR(255),
    createDate DATETIME NOT NULL,
    modifiedDate DATETIME NOT NULL,
    CONSTRAINT FK_Wishlist_User FOREIGN KEY (user_id) REFERENCES Users(userId) ON DELETE CASCADE
);