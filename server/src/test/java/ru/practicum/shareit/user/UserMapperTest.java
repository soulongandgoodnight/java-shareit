package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    void toDto_shouldMapAllFields() {
        User user = new User();
        user.setId(1L);
        user.setName("Lexa");
        user.setEmail("lexa@test.com");

        UserDto dto = mapper.toDto(user);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Lexa");
        assertThat(dto.getEmail()).isEqualTo("lexa@test.com");
    }

    @Test
    void toEntity_shouldMapAllFields() {
        UserDto dto = new UserDto();
        dto.setId(1L);
        dto.setName("Tima");
        dto.setEmail("tima@test.com");

        User user = mapper.toEntity(dto);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getName()).isEqualTo("Tima");
        assertThat(user.getEmail()).isEqualTo("tima@test.com");
    }

    @Test
    void updateFromDto_shouldUpdateOnlyNonNullNonEmptyFields() {
        User user = new User();
        user.setName("Старое имя");
        user.setEmail("old@test.com");

        UserDto dto = new UserDto();
        dto.setName("Новое имя");

        mapper.updateFromDto(user, dto);

        assertThat(user.getName()).isEqualTo("Новое имя");
        assertThat(user.getEmail()).isEqualTo("old@test.com");
    }

    @Test
    void updateFromDto_shouldUpdateEmail() {
        User user = new User();
        user.setName("Tima");
        user.setEmail("old@test.com");

        UserDto dto = new UserDto();
        dto.setEmail("new@test.com");

        mapper.updateFromDto(user, dto);

        assertThat(user.getEmail()).isEqualTo("new@test.com");
        assertThat(user.getName()).isEqualTo("Tima");
    }

    @Test
    void updateFromDto_withEmptyName_shouldNotUpdate() {
        User user = new User();
        user.setName("Lexa");
        user.setEmail("lexa@test.com");

        UserDto dto = new UserDto();
        dto.setName("  ");

        mapper.updateFromDto(user, dto);

        assertThat(user.getName()).isEqualTo("Lexa");
    }
}