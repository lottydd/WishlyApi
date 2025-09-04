package com.example.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParseRequestDTO {
    // Ссылка на товар
    private String url;

    // ID пользователя, который отправил запрос (опционально, если нужен для сохранения)
    private Integer userId;

    // ID списка желаемого (wishlist), если нужно знать, куда сохранить результат
    private Integer wishlistId;
}