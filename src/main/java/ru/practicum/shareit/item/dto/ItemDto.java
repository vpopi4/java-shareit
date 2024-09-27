package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.NotBlankIfNotNull;

public class ItemDto {
    public static class Request {
        @Value
        @Builder
        public static class Create {
            @NotBlank String name;
            @NotBlank String description;
            @NotNull Boolean available;
        }

        @Value
        @Builder
        public static class UpdatePartially {
            @NotBlankIfNotNull
            String name;
            @NotBlankIfNotNull
            String description;
            Boolean available;
        }
    }

    public static class Response {
        @Value
        @Builder
        public static class PublicInfo {
            Integer id;
            String name;
            String description;
            Boolean available;
        }
    }
}
