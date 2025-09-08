INSERT INTO Roles (roleName) VALUES
('ROLE_USER'),
('ROLE_ADMIN');

-- Fill Users table
INSERT INTO Users (username, email, description, password) VALUES
('ivan_petrov', 'ivan.petrov@mail.com', 'Regular user interested in technology', 'hashed_password_1'),
('anna_sidorova', 'anna.sidorova@mail.com', 'Book lover and collector', 'hashed_password_2'),
('sergey_kozlov', 'sergey.kozlov@mail.com', 'Sports enthusiast and traveler', 'hashed_password_3'),
('maria_ivanova', 'maria.ivanova@mail.com', 'Professional photographer', 'hashed_password_4'),
('alexey_fedorov', 'alexey.fedorov@mail.com', 'Software developer and gamer', 'hashed_password_5');

-- Assign roles to users
INSERT INTO UserRoles (user_id, role_id) VALUES
(1, 1),  -- ivan_petrov - User
(2, 1),  -- anna_sidorova - User
(3, 1),  -- sergey_kozlov - User
(4, 1),  -- maria_ivanova - User
(5, 1),  -- alexey_fedorov - User
(5, 2);  -- alexey_fedorov - Admin

-- Insert items
INSERT INTO Items (itemName, description, price, imageUrl, sourceUrl, owner_id) VALUES
('iPhone 15 Pro', 'Latest Apple smartphone with A17 Pro chip', 999.99, 'https://example.com/iphone15.jpg', 'https://apple.com/iphone', 1),
('Sony WH-1000XM5', 'Wireless noise-canceling headphones', 349.99, 'https://example.com/sonyxm5.jpg', 'https://sony.com/headphones', 2),
('Kindle Paperwhite', 'E-reader with 6.8" display and backlight', 139.99, 'https://example.com/kindle.jpg', 'https://amazon.com/kindle', 2),
('Nikon Z7 II', 'Mirrorless camera with 45.7MP sensor', 2999.99, 'https://example.com/nikonz7.jpg', 'https://nikon.com/cameras', 4),
('PlayStation 5', 'Gaming console with 4K/120fps support', 499.99, 'https://example.com/ps5.jpg', 'https://sony.com/ps5', 5),
('MacBook Pro 16"', 'Laptop with M3 Max chip for professionals', 2499.99, 'https://example.com/macbook.jpg', 'https://apple.com/macbook', 5),
('Dyson V15 Detect', 'Cordless vacuum cleaner with laser dust detection', 699.99, 'https://example.com/dyson.jpg', 'https://dyson.com/vacuums', 3),
('Rolex Submariner', 'Luxury diving watch with automatic movement', 8999.99, 'https://example.com/rolex.jpg', 'https://rolex.com/watches', 1);

-- Create wishlists
INSERT INTO Wishlists (userId, wishlistName, wishlistDescription, createDate, modifiedDate) VALUES
(1, 'Tech Wishlist', 'Latest gadgets and electronics', '2024-01-15 10:30:00', '2024-03-20 14:25:00'),
(2, 'Reading Collection', 'Books and reading devices', '2024-02-01 09:15:00', '2024-03-18 11:40:00'),
(3, 'Home Appliances', 'For comfortable living', '2024-01-20 16:45:00', '2024-03-19 09:30:00'),
(4, 'Photography Gear', 'Professional camera equipment', '2024-02-10 13:20:00', '2024-03-21 17:15:00'),
(5, 'Gaming Setup', 'Everything for perfect gaming experience', '2024-01-25 11:00:00', '2024-03-22 10:45:00'),
(2, 'Birthday Wishes', 'Gifts for my birthday', '2024-03-01 08:00:00', '2024-03-15 12:30:00');

-- Link items to wishlists
INSERT INTO WishlistItems (wishlistId, itemId) VALUES
(1, 1),  -- iPhone в Tech Wishlist
(1, 2),  -- Sony headphones в Tech Wishlist
(2, 3),  -- Kindle в Reading Collection
(4, 4),  -- Nikon camera в Photography Gear
(5, 5),  -- PlayStation в Gaming Setup
(5, 6),  -- MacBook в Gaming Setup
(3, 7),  -- Dyson vacuum в Home Appliances
(6, 2),  -- Sony headphones в Birthday Wishes
(6, 3),  -- Kindle в Birthday Wishes
(6, 8);  -- Rolex в Birthday Wishes