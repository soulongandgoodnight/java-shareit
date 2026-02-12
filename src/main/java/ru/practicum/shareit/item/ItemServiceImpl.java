package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private final Map<Long, List<Item>> ownerToItems = new ConcurrentHashMap<>();
    private final ItemMapper mapper;
    private final UserService userService;
    private long idCounter = 1;

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        User owner = userService.getUserEntityById(userId);

        Item item = mapper.toEntity(itemDto);
        item.setId(idCounter++);
        item.setOwner(owner);

        items.put(item.getId(), item);
        ownerToItems.computeIfAbsent(userId, k -> new ArrayList<>()).add(item);

        return mapper.toDto(item);
    }

    @Override
    public ItemDto update(Long itemId,ItemDto itemDto,Long userId) {
        Item item = getItemById(itemId);

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Только владелец может редактировать вещь");
        }

        mapper.updateFromDto(item, itemDto);
        return mapper.toDto(item);
    }

    @Override
    public ItemDto getById(Long itemId) {
        return mapper.toDto(getItemById(itemId));
    }

    @Override
    public List<ItemDto> getAllByOwner(Long userId) {
        userService.getUserEntityById(userId);
        return ownerToItems.getOrDefault(userId, Collections.emptyList())
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String lowerText = text.toLowerCase();

        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item ->
                        (item.getName() != null && item.getName().toLowerCase().contains(lowerText)) ||
                                (item.getDescription() != null && item.getDescription().toLowerCase()
                                        .contains(lowerText))
                )
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    private Item getItemById(Long id) {
        return items.values().stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Вещь с id " + id + " не найдена"));
    }
}
