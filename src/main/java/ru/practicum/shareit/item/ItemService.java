package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(Long itemId, ItemDto itemDto, Long userId);

    ItemDto getById(Long itemId, Long userId);

    List<ItemDto> getAllByOwner(Long userId);

    List<ItemDto> search(String text);

    CommentDto createComment(Long itemId, Long userId, CommentDto commentDto);
}
