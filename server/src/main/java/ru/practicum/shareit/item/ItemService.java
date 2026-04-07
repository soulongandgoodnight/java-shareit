package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.List;

public interface ItemService {
    ItemDto create(ItemRequestDto itemRequest, Long userId);

    ItemDto update(Long itemId, ItemRequestDto itemRequest, Long userId);

    ItemDto getById(Long itemId, Long userId);

    List<ItemDto> getAllByOwner(Long userId);

    List<ItemDto> search(String text);

    CommentDto createComment(Long itemId, Long userId, CommentRequestDto commentRequestDto);
}
