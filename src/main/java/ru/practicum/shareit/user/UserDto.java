package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;

public class UserDto {
    private interface IdField {
        Integer getId();
    }

    private interface EmailField {
        @Email
        String getEmail();
    }

    private interface NameField {
        String getName();
    }

    public static class Request {
        @Value
        @Builder
        public static class Create implements EmailField, NameField {
            @NotNull String email;
            @NotNull String name;
        }

        @Value
        @Builder
        public static class UpdatePartially implements EmailField, NameField {
            String email;
            String name;
        }
    }

    public static class Response {
        @Value
        @Builder
        public static class PublicInfo implements IdField, EmailField, NameField {
            Integer id;
            String email;
            String name;
        }
    }
}
