CREATE TABLE UserRoles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY(user_id, role_id),
    CONSTRAINT FK_UserRoles_User FOREIGN KEY (user_id) REFERENCES Users(userId) ON DELETE CASCADE,
    CONSTRAINT FK_UserRoles_Role FOREIGN KEY (role_id) REFERENCES Roles(roleId) ON DELETE CASCADE
);