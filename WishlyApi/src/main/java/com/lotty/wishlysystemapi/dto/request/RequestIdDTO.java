package com.lotty.wishlysystemapi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestIdDTO {

    @NotNull(message = "Передаваемый ID не может быть равен нулю")
    private Integer id;
}
