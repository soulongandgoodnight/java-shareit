package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.dto.UserDto;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        return dto;
    }

    public User toEntity(UserDto dto) {
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        return user;
    }

    public void updateFromDto(User user, UserDto dto) {
        if (dto.getName() != null && !dto.getName().trim().isEmpty()) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            user.setEmail(dto.getEmail());
        }
    }
}
