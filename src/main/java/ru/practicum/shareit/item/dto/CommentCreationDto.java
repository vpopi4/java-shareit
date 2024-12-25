package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CommentCreationDto {
    @NotBlank
    String text;

    @JsonCreator
    public CommentCreationDto(@JsonProperty("text") String text) {
        this.text = text;
    }
}
