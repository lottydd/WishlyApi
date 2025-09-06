package com.example.common.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ParseRequestDTO {
    // Ссылка на товар
    @NotBlank
    private String url;

    @NotNull
    // ID пользователя, который отправил запрос (опционально, если нужен для сохранения)
    private Integer userId;

    // ID списка желаемого (wishlist), если нужно знать, куда сохранить результат
    @NotNull
    private Integer wishlistId;
    @NotNull
    private Integer taskId;

}