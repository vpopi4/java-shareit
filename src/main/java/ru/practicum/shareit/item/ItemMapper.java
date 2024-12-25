package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class ItemMapper {
    public Item toItem(ItemDto.Request.Create dto, User owner) {
        return Item.builder()
                .id(null)
                .name(dto.getName())
                .description(dto.getDescription())
                .isAvailable(dto.getAvailable())
                .owner(owner)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public ItemDto.Response.PublicInfo toDto(Item item) {
        return ItemDto.Response.PublicInfo.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .comments(item.getComments() == null
                        ? null
                        : item.getComments()
                        .stream()
                        .map(this::toCommentDto)
                        .toList())
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
