package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Test
    void create_shouldReturnCreatedBooking() throws Exception {
        BookingRequestDto dto = new BookingRequestDto();
        dto.setItemId(1L);
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));

        BookingDto response = new BookingDto();
        response.setId(1L);
        response.setStatus(BookingStatus.WAITING);

        when(bookingService.create(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void approve_shouldReturnApprovedBooking() throws Exception {
        BookingDto response = new BookingDto();
        response.setId(1L);
        response.setStatus(BookingStatus.APPROVED);

        when(bookingService.approve(1L, 1L, true)).thenReturn(response);

        mockMvc.perform(patch("/bookings/1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void getById_shouldReturnBooking() throws Exception {
        BookingDto response = new BookingDto();
        response.setId(1L);
        response.setStatus(BookingStatus.WAITING);

        when(bookingService.getById(1L, 1L)).thenReturn(response);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getAllByUser_shouldReturnList() throws Exception {
        BookingDto response = new BookingDto();
        response.setId(1L);

        when(bookingService.getAllByUser(eq(1L), eq(BookingState.ALL)))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllByUser_whenWrongStatus_shouldReturnBadRequest() throws Exception {
        BookingDto response = new BookingDto();
        response.setId(1L);

        when(bookingService.getAllByUser(eq(1L), eq(BookingState.ALL)))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "Wrong state"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllByOwner_shouldReturnList() throws Exception {
        BookingDto response = new BookingDto();
        response.setId(1L);

        when(bookingService.getAllByOwner(eq(1L), eq(BookingState.ALL)))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
