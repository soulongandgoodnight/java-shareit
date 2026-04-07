package ru.practicum.shareit.booking;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {
    public BookingDto toDto(Booking booking) {

        BookingDto dto = new BookingDto();

        dto.setId(booking.getId());
        dto.setStart(booking.getStart());
        dto.setEnd(booking.getEnd());
        dto.setStatus(booking.getStatus());

        BookingDto.BookerDto booker = new BookingDto.BookerDto();
        booker.setId(booking.getBooker().getId());
        dto.setBooker(booker);

        BookingDto.ItemDto item = new BookingDto.ItemDto();
        item.setId(booking.getItem().getId());
        item.setName(booking.getItem().getName());
        dto.setItem(item);

        return dto;
    }
}
