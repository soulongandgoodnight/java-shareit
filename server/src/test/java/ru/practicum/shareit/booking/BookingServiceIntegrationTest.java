package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    private Long ownerId;
    private Long bookerId;
    private Long itemId;

    @BeforeEach
    void setUp() {
        UserDto owner = new UserDto();
        owner.setName("Owner");
        owner.setEmail("owner@example.com");
        ownerId = userService.create(owner).getId();

        UserDto booker = new UserDto();
        booker.setName("Booker");
        booker.setEmail("booker@example.com");
        bookerId = userService.create(booker).getId();

        ItemRequestDto item = new ItemRequestDto();
        item.setName("Дрель");
        item.setDescription("Мощная дрель");
        item.setAvailable(true);
        itemId = itemService.create(item, ownerId).getId();

        BookingRequestDto booking = new BookingRequestDto();
        booking.setItemId(itemId);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        bookingService.create(bookerId, booking);
    }

    @Test
    void getAllByUser_shouldReturnBookerBookings() {
        List<BookingDto> bookings = bookingService.getAllByUser(bookerId, BookingState.ALL);

        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(itemId);
    }
}
