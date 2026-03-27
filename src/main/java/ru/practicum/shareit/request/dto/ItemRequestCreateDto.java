package ru.practicum.shareit.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ItemRequestCreateDto {

    @NotBlank(message = "Описание не может быть пустым")
    @Size(max = 2048)
    private String description;
}
