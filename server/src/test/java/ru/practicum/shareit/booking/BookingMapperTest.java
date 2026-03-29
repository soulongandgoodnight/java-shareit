package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingMapperTest {

    private final BookingMapper mapper = new BookingMapper();

    @Test
    void toDto_shouldMapAllFields() {
        User owner = new User();
        owner.setId(1L);
        owner.setName("Owner");
        owner.setEmail("owner@test.com");

        Item item = new Item();
        item.setId(10L);
        item.setName("Дрель");
        item.setDescription("Описание");
        item.setAvailable(true);
        item.setOwner(owner);

        User booker = new User();
        booker.setId(2L);
        booker.setName("Booker");
        booker.setEmail("booker@test.com");

        Booking booking = new Booking();
        booking.setId(100L);
        booking.setStart(LocalDateTime.of(2025, 1, 1, 10, 0));
        booking.setEnd(LocalDateTime.of(2025, 1, 2, 10, 0));
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);

        BookingDto dto = mapper.toDto(booking);

        assertThat(dto.getId()).isEqualTo(100L);
        assertThat(dto.getStart()).isEqualTo(LocalDateTime.of(2025, 1, 1, 10, 0));
        assertThat(dto.getEnd()).isEqualTo(LocalDateTime.of(2025, 1, 2, 10, 0));
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.WAITING);
        assertThat(dto.getBooker().getId()).isEqualTo(2L);
        assertThat(dto.getItem().getId()).isEqualTo(10L);
        assertThat(dto.getItem().getName()).isEqualTo("Дрель");
    }
}
