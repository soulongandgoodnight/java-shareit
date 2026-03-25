package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

public interface BookingService {

    BookingDto create(Long userId, BookingRequestDto request);

    BookingDto approve(Long bookingId, Long ownerId, boolean approved);

    BookingDto getById(Long bookingId, Long userId);

    List<BookingDto> getAllByUser(Long userId, String state);

    List<BookingDto> getAllByOwner(Long ownerId, String state);
}
