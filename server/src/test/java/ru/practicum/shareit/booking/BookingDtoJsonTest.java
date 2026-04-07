package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    private JacksonTester<BookingDto> json;

    @Test
    void serialize_shouldFormatDateCorrectly() throws Exception {
        BookingDto dto = new BookingDto();
        dto.setId(1L);
        dto.setStart(LocalDateTime.of(2025, 6, 1, 12, 0, 0));
        dto.setEnd(LocalDateTime.of(2025, 6, 2, 12, 0, 0));
        dto.setStatus(BookingStatus.WAITING);

        var result = json.write(dto);

        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathStringValue("$.status")
                .isEqualTo("WAITING");
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo("2025-06-01T12:00:00");
    }

    @Test
    void deserialize_shouldReadStatusCorrectly() throws Exception {
        String content = "{\"id\": 1, \"start\": \"2025-06-01T12:00:00\", \"end\": \"2025-06-02T12:00:00\", \"status\": \"APPROVED\"}";

        BookingDto dto = json.parseObject(content);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getStatus()).isEqualTo(BookingStatus.APPROVED);
    }
}
