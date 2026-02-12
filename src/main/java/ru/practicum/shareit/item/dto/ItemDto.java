package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {
    private Long id;
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @NotBlank(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Доступность должна быть указана")
    private Boolean available;
}
