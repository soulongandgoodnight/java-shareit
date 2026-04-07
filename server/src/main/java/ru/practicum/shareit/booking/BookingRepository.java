package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findByBookerIdAndStartBeforeAndEndAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndEndBefore(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartAfter(Long bookerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findByItemOwnerIdAndStatus(Long ownerId, BookingStatus status, Sort sort);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfter(Long ownerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByItemOwnerIdAndEndBefore(Long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndStartAfter(Long ownerId, LocalDateTime now, Sort sort);

    Booking findFirstByItemIdAndEndBeforeOrderByEndDesc(Long itemId, LocalDateTime now);

    Booking findFirstByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    boolean existsByItemIdAndBookerIdAndEndBefore(Long itemId, Long bookerId, LocalDateTime now);
}