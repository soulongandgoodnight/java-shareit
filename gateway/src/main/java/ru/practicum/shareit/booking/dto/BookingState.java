package ru.practicum.shareit.booking.dto;

public enum BookingState {
    ALL, CURRENT, FUTURE, PAST, REJECTED, WAITING;

    public static BookingState fromString(String state) {
        for (BookingState s : values()) {
            if (s.name().equalsIgnoreCase(state)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown state: " + state);
    }
}