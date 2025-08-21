package com.lotty.wishlysystemapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Запрос, содержащий только ID")
public class RequestIdDTO {
    @NotNull(message = "Передаваемый ID не может быть равен нулю")
    @Schema(description = "ID сущности", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer id;
}
