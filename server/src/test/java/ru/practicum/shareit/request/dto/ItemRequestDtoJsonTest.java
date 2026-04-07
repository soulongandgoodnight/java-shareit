package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {

    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    void serialize_shouldContainItems() throws Exception {
        var dto = new ItemRequestDto();
        var itemDto = new ItemDto();
        itemDto.setName("Нужна дрель");
        var itemsDto = new ArrayList<ItemDto>();
        itemsDto.add(itemDto);
        dto.setItems(itemsDto);

        var result = json.write(dto);
        assertThat(result).hasJsonPath("$.items");
        assertThat(result).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo("Нужна дрель");
    }
}
