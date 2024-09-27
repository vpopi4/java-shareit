package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

@Component
public class ItemMapper {
    public Item toItem(Integer id, ItemDto.Request.Create dto, User owner) {
        return Item.builder()
                .id(id)
                .name(dto.getName())
                .description(dto.getDescription())
                .isAvailable(dto.getAvailable())
                .owner(owner)
                .build();
    }

    public ItemDto.Response.PublicInfo toDto(Item item) {
        return ItemDto.Response.PublicInfo.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getIsAvailable())
                .build();
    }
}
