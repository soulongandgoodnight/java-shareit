package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
public class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService requestService;

    @Test
    void create_shouldReturnCreatedRequest() throws Exception {
        ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Нужна дрель");

        ItemRequestDto response = new ItemRequestDto();
        response.setId(1L);
        response.setDescription("Нужна дрель");
        response.setCreated(LocalDateTime.now());

        when(requestService.create(eq(1L), any())).thenReturn(response);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value("Нужна дрель"));
    }

    @Test
    void getAllByUser_shouldReturnList() throws Exception {
        ItemRequestDto response = new ItemRequestDto();
        response.setId(1L);
        response.setDescription("Нужна дрель");

        when(requestService.getAllByUser(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getAllOthers_shouldReturnList() throws Exception {
        ItemRequestDto response = new ItemRequestDto();
        response.setId(2L);
        response.setDescription("Нужен молоток");

        when(requestService.getAllOthers(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getById_shouldReturnRequest() throws Exception {
        ItemRequestDto response = new ItemRequestDto();
        response.setId(1L);
        response.setDescription("Нужна дрель");

        when(requestService.getById(1L, 1L)).thenReturn(response);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
