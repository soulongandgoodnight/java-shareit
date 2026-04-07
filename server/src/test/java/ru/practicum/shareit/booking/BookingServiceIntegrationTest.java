package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {

    private final BookingService bookingService;
    private final ItemService itemService;
    private final UserService userService;

    private Long ownerId;
    private Long bookerId;
    private Long itemId;
    private Long bookingId;

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
        bookingId = bookingService.create(bookerId, booking).getId();
    }

    @Test
    void getAllByUser_stateAll_shouldReturnBookings() {
        List<BookingDto> bookings = bookingService.getAllByUser(bookerId, BookingState.ALL);
        assertThat(bookings).hasSize(1);
        assertThat(bookings.get(0).getItem().getId()).isEqualTo(itemId);
    }

    @Test
    void getAllByUser_stateFuture_shouldReturnBookings() {
        List<BookingDto> bookings = bookingService.getAllByUser(bookerId, BookingState.FUTURE);
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getAllByUser_statePast_shouldReturnEmpty() {
        List<BookingDto> bookings = bookingService.getAllByUser(bookerId, BookingState.PAST);
        assertThat(bookings).isEmpty();
    }

    @Test
    void getAllByUser_stateWaiting_shouldReturnBookings() {
        List<BookingDto> bookings = bookingService.getAllByUser(bookerId, BookingState.WAITING);
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getAllByUser_stateRejected_shouldReturnEmpty() {
        List<BookingDto> bookings = bookingService.getAllByUser(bookerId, BookingState.REJECTED);
        assertThat(bookings).isEmpty();
    }

    @Test
    void getAllByOwner_stateAll_shouldReturnBookings() {
        List<BookingDto> bookings = bookingService.getAllByOwner(ownerId, BookingState.ALL);
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getAllByOwner_stateFuture_shouldReturnBookings() {
        List<BookingDto> bookings = bookingService.getAllByOwner(ownerId, BookingState.FUTURE);
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getAllByOwner_stateCurrent_shouldReturnEmpty() {
        List<BookingDto> bookings = bookingService.getAllByOwner(ownerId, BookingState.CURRENT);
        assertThat(bookings).isEmpty();
    }

    @Test
    void getAllByOwner_statePast_shouldReturnEmpty() {
        List<BookingDto> bookings = bookingService.getAllByOwner(ownerId, BookingState.PAST);
        assertThat(bookings).isEmpty();
    }

    @Test
    void getAllByOwner_stateWaiting_shouldReturnBookings() {
        List<BookingDto> bookings = bookingService.getAllByOwner(ownerId, BookingState.WAITING);
        assertThat(bookings).hasSize(1);
    }

    @Test
    void getAllByOwner_stateRejected_shouldReturnEmpty() {
        List<BookingDto> bookings = bookingService.getAllByOwner(ownerId, BookingState.REJECTED);
        assertThat(bookings).isEmpty();
    }

    @Test
    void approve_shouldChangeStatusToApproved() {
        BookingDto approved = bookingService.approve(bookingId, ownerId, true);
        assertThat(approved.getStatus().name()).isEqualTo("APPROVED");
    }

    @Test
    void approve_whenNotOwnerTriesToApprove_shouldThrowValidationException() {
        assertThatThrownBy(() -> bookingService.approve(bookingId, bookerId, true))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void approve_whenTriesToApproveBookedItem_shouldThrowValidationException() {
        BookingDto approved = bookingService.approve(bookingId, ownerId, true);

        assertThatThrownBy(() -> bookingService.approve(bookingId, ownerId, true))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void approve_shouldChangeStatusToRejected() {
        BookingDto rejected = bookingService.approve(bookingId, ownerId, false);
        assertThat(rejected.getStatus().name()).isEqualTo("REJECTED");
    }

    @Test
    void getById_shouldReturnBooking() {
        BookingDto booking = bookingService.getById(bookingId, bookerId);
        assertThat(booking.getId()).isEqualTo(bookingId);
    }

    @Test
    void create_whenStartIsBiggerThanEnd_shouldThrowValidationException() {
        var request = new BookingRequestDto();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(3));
        request.setEnd(LocalDateTime.now().plusDays(1));
        assertThatThrownBy(() -> bookingService.create(bookerId, request))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void create_whenOwnerTriesToBookItsOwnItem_shouldThrowValidationException() {
        var request = new BookingRequestDto();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(3));
        assertThatThrownBy(() -> bookingService.create(ownerId, request))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void create_whenItemIsNotAvailableForBooking_shouldThrowValidationException() {
        ItemRequestDto item = new ItemRequestDto();
        item.setName("Прищепка");
        item.setDescription("Для носа");
        item.setAvailable(false);
        itemId = itemService.create(item, ownerId).getId();

        var request = new BookingRequestDto();
        request.setItemId(itemId);
        request.setStart(LocalDateTime.now().plusDays(1));
        request.setEnd(LocalDateTime.now().plusDays(3));
        assertThatThrownBy(() -> bookingService.create(bookerId, request))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void getById_shouldThrowNotFoundException() {
        assertThatThrownBy(() -> bookingService.getById(bookingId, -1L)).isInstanceOf(NotFoundException.class);
    }
}