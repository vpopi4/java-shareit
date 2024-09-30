package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface ItemService {
    /**
     * This method creates `Item`.
     *
     * @param userId is an Integer extracted from the "X-Sharer-User-Id" request header
     * @param dto    is a `ItemDto.Request.Create` extracted from request body
     * @return `ItemDto.Response.PublicInfo` object
     */
    ItemDto.Response.PublicInfo createItem(Integer userId,
                                           ItemDto.Request.Create dto);

    /**
     * This method allows to partially update an existing `Item` by its owner.
     *
     * @param userId is an Integer extracted from the "X-Sharer-User-Id" request header
     * @param itemId is an Integer extracted from the "itemId" path variable
     * @param dto    is a `ItemDto.Request.UpdatePartially` extracted from the request body
     * @return `ItemDto.Response.PublicInfo` object
     */
    ItemDto.Response.PublicInfo updatePartially(Integer userId,
                                                Integer itemId,
                                                ItemDto.Request.UpdatePartially dto) throws AccessDeniedException;

    /**
     * This method returns a public info about the `Item`.
     *
     * @param itemId is an Integer extracted from the "itemId" path variable
     * @return `ItemDto.Response.PublicInfo` object
     */
    ItemDto.Response.PublicInfo getById(Integer itemId);

    /**
     * This method returns a list of `Item` owned by the user.
     *
     * @param userId is an Integer extracted from the "X-Sharer-User-Id" request header
     * @return list of `ItemDto.Response.PublicInfo`
     */
    List<ItemDto.Response.PublicInfo> getAllByUserId(Integer userId);

    /**
     * This method returns available for booking list of `Item`
     * that contain the `text` in their name or description.
     *
     * @param text is a String extracted from the request param
     * @return list of `ItemDto.Response.PublicInfo`
     */
    List<ItemDto.Response.PublicInfo> search(String text);
}
