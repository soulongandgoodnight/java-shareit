package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingMapper mapper;

    private static final Sort SORT_NEWEST_FIRST = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingRequestDto request) {

        User booker = userService.getUserEntityById(userId);

        Item item = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (!item.getAvailable()) {
            throw new ValidationException("Вещь недоступна для бронирования");
        }

        if (!request.getStart().isBefore(request.getEnd())) {
            throw new ValidationException("Дата начала должна быть раньше даты окончания");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Владелец не может бронировать свою вещь");
        }

        Booking booking = new Booking();
        booking.setStart(request.getStart());
        booking.setEnd(request.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        booking = bookingRepository.save(booking);

        return mapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approve(Long bookingId, Long ownerId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new ValidationException("Только владелец может подтверждать бронирование");
        }

        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ValidationException("Бронирование уже обработано");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        booking = bookingRepository.save(booking);

        return mapper.toDto(booking);
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Нет доступа");
        }

        return mapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getAllByUser(Long userId, BookingState state) {
        userService.getUserEntityById(userId);

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByBookerId(userId, SORT_NEWEST_FIRST);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(userId, now, now, SORT_NEWEST_FIRST);
                break;
            case PAST:
                bookings = bookingRepository.findByBookerIdAndEndBefore(userId, now, SORT_NEWEST_FIRST);
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerIdAndStartAfter(userId, now, SORT_NEWEST_FIRST);
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.WAITING, SORT_NEWEST_FIRST);
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerIdAndStatus(userId, BookingStatus.REJECTED, SORT_NEWEST_FIRST);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByOwner(Long ownerId, BookingState state) {
        userService.getUserEntityById(ownerId);

        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings;

        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerId(ownerId, SORT_NEWEST_FIRST);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(ownerId, now, now, SORT_NEWEST_FIRST);
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndBefore(ownerId, now, SORT_NEWEST_FIRST);
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartAfter(ownerId, now, SORT_NEWEST_FIRST);
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.WAITING, SORT_NEWEST_FIRST);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatus(ownerId, BookingStatus.REJECTED, SORT_NEWEST_FIRST);
                break;
            default:
                throw new ValidationException("Unknown state: " + state);
        }

        return bookings.stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }
}