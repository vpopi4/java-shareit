package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemCreatingOrUpdatingDto;
import ru.practicum.shareit.item.dto.ItemPublicDto;
import ru.practicum.shareit.util.ClientException;

import java.util.List;

public interface ItemService {
    /**
     * This method creates `Item`.
     *
     * @param userId is an Integer extracted from the "X-Sharer-User-Id" request header
     * @param dto    is a `ItemCreatingOrUpdatingDto` extracted from request body
     * @return `ItemPublicDto` object
     */
    ItemPublicDto createItem(Integer userId,
                             ItemCreatingOrUpdatingDto dto) throws ClientException;

    /**
     * This method allows to partially update an existing `Item` by its owner.
     *
     * @param userId is an Integer extracted from the "X-Sharer-User-Id" request header
     * @param itemId is an Integer extracted from the "itemId" path variable
     * @param dto    is a `ItemCreatingOrUpdatingDto` extracted from the request body
     * @return `ItemPublicDto` object
     */
    ItemPublicDto updatePartially(Integer userId,
                                  Integer itemId,
                                  ItemCreatingOrUpdatingDto dto) throws ClientException;

    /**
     * This method returns a public info about the `Item`.
     *
     * @param itemId is an Integer extracted from the "itemId" path variable
     * @return `ItemPublicDto` object
     */
    ItemPublicDto getById(Integer itemId) throws ClientException;

    /**
     * This method returns a list of `Item` owned by the user.
     *
     * @param userId is an Integer extracted from the "X-Sharer-User-Id" request header
     * @return list of `ItemPublicDto`
     */
    List<ItemPublicDto> getAllByUserId(Integer userId);

    /**
     * This method returns available for booking list of `Item`
     * that contain the `text` in their name or description.
     *
     * @param text is a String extracted from the request param
     * @return list of `ItemPublicDto`
     */
    List<ItemPublicDto> search(String text);

    CommentDto postComment(Integer userId, Integer itemId, CommentCreationDto dto) throws ClientException;
}
