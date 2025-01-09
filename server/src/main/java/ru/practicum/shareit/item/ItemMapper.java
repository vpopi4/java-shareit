package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemPublicDto;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public ItemPublicDto toDto(Item item) {
        return toDto(item, null, null);
    }

    public ItemPublicDto toDto(Item item, BookingDto last, BookingDto next) {
        return ItemPublicDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .comments(item.getComments()
                        .stream()
                        .map(this::toCommentDto)
                        .toList())
                .lastBooking(last)
                .nextBooking(next)
                .build();
    }

    public ItemShortDto toShortDto(Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreatedAt())
                .build();
    }
}
