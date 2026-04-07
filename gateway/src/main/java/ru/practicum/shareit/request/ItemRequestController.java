package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestClient requestClient;
    private static final String USER_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Object> create(@RequestHeader(USER_HEADER) long userId,
                                         @Valid @RequestBody ItemRequestCreateDto dto) {
        return requestClient.create(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(USER_HEADER) long userId) {
        return requestClient.getAllByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllOthers(@RequestHeader(USER_HEADER) long userId) {
        return requestClient.getAllOthers(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@RequestHeader(USER_HEADER) long userId,
                                          @PathVariable long requestId) {
        return requestClient.getById(userId, requestId);
    }
}