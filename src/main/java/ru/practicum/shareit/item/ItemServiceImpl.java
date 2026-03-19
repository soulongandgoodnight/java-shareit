package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemMapper mapper;
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        User owner = userService.getUserEntityById(userId);

        Item item = mapper.toEntity(itemDto);
        item.setOwner(owner);

        item = itemRepository.save(item);

        return mapper.toDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(Long itemId, ItemDto itemDto, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Только владелец может редактировать вещь");
        }

        mapper.updateFromDto(item, itemDto);
        item = itemRepository.save(item);
        return mapper.toDto(item);
    }

    @Override
    public ItemDto getById(Long itemId, Long userId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        ItemDto dto = mapper.toDto(item);

        if (item.getOwner().getId().equals(userId)) {

            LocalDateTime now = LocalDateTime.now();

            Booking last = bookingRepository
                    .findFirstByItemIdAndEndBeforeOrderByEndDesc(itemId, now);

            Booking next = bookingRepository
                    .findFirstByItemIdAndStartAfterOrderByStartAsc(itemId, now);

            if (last != null) dto.setLastBooking(last.getStart());
            if (next != null) dto.setNextBooking(next.getStart());
        }

        List<Comment> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId);

        dto.setComments(
                comments.stream()
                        .map(commentMapper::toDto)
                        .collect(Collectors.toList())
        );

        return dto;
    }

    @Override
    public List<ItemDto> getAllByOwner(Long userId) {
        userService.getUserEntityById(userId);
        return itemRepository.findByOwnerIdOrderById(userId).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }

        String lowerText = text.toLowerCase();

        return itemRepository.searchAvailable(lowerText).stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(Long itemId, Long userId, CommentDto commentDto) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        User author = userService.getUserEntityById(userId);

        LocalDateTime now = LocalDateTime.now();

        boolean hasPastBooking = bookingRepository
                .existsByItemIdAndBookerIdAndEndBefore(itemId, userId, now);

        if (!hasPastBooking) {
            throw new ValidationException("Пользователь не может оставлять комментарий");
        }

        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());

        comment = commentRepository.save(comment);

        return commentMapper.toDto(comment);
    }
}