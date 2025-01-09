package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ItemRequestCreationDto {
    String description;

    @JsonCreator
    public ItemRequestCreationDto(@JsonProperty("description") String description) {
        this.description = description;
    }
}
