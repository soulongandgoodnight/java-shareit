package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemRequestMapper mapper;

    private static final Sort SORT_NEWEST = Sort.by(Sort.Direction.DESC, "created");

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestCreateDto dto) {
        User requestor = userService.getUserEntityById(userId);

        ItemRequest request = new ItemRequest();
        request.setDescription(dto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());

        request = requestRepository.save(request);
        return mapper.toDto(request);
    }

    @Override
    public List<ItemRequestDto> getAllByUser(Long userId) {
        userService.getUserEntityById(userId);

        List<ItemRequest> requests = requestRepository.findByRequestorId(userId, SORT_NEWEST);
        return fillWithItems(requests);
    }

    @Override
    public List<ItemRequestDto> getAllOthers(Long userId) {
        userService.getUserEntityById(userId);

        List<ItemRequest> requests = requestRepository.findByRequestorIdNot(userId, SORT_NEWEST);
        return fillWithItems(requests);
    }

    @Override
    public ItemRequestDto getById(Long requestId, Long userId) {
        userService.getUserEntityById(userId);

        ItemRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос не найден"));

        ItemRequestDto dto = mapper.toDto(request);

        List<Item> items = itemRepository.findByRequestId(requestId);
        dto.setItems(items.stream().map(this::toItemDto).collect(Collectors.toList()));

        return dto;
    }

    private List<ItemRequestDto> fillWithItems(List<ItemRequest> requests) {
        List<Long> requestIds = requests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> allItems = itemRepository.findByRequestIdIn(requestIds);

        Map<Long, List<Item>> itemsByRequest = allItems.stream()
                .collect(Collectors.groupingBy(Item::getRequestId));

        return requests.stream().map(req -> {
            ItemRequestDto dto = mapper.toDto(req);
            List<Item> items = itemsByRequest.getOrDefault(req.getId(), List.of());
            dto.setItems(items.stream().map(this::toItemDto).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
    }

    private ItemRequestDto.ItemDto toItemDto(Item item) {
        ItemRequestDto.ItemDto dto = new ItemRequestDto.ItemDto();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setOwnerId(item.getOwner().getId());
        return dto;
    }
}
