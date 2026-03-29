package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void create_shouldReturnCreatedUser() throws Exception {
        UserDto dto = new UserDto();
        dto.setName("Tima");
        dto.setEmail("tima@example.com");

        UserDto response = new UserDto();
        response.setId(1L);
        response.setName("Tima");
        response.setEmail("tima@example.com");

        when(userService.create(any())).thenReturn(response);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Tima"))
                .andExpect(jsonPath("$.email").value("tima@example.com"));
    }

    @Test
    void getById_shouldReturnUser() throws Exception {
        UserDto response = new UserDto();
        response.setId(1L);
        response.setName("Tima");
        response.setEmail("tima@example.com");

        when(userService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Tima"));
    }

    @Test
    void getById_notFound_shouldReturn404() throws Exception {
        when(userService.getById(99L))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_shouldReturnList() throws Exception {
        UserDto u1 = new UserDto();
        u1.setId(1L);
        u1.setName("Tima");
        u1.setEmail("tima@example.com");

        when(userService.getAll()).thenReturn(List.of(u1));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void update_shouldReturnUpdatedUser() throws Exception {
        UserDto dto = new UserDto();
        dto.setName("Updated");
        dto.setEmail("updated@example.com");

        UserDto response = new UserDto();
        response.setId(1L);
        response.setName("Updated");
        response.setEmail("updated@example.com");

        when(userService.update(eq(1L), any())).thenReturn(response);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        doNothing().when(userService).delete(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }
}
