package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequestDto {

    @NotBlank(message = "Текст комментария не может быть пустым")
    @Size(max = 2048)
    private String text;
}