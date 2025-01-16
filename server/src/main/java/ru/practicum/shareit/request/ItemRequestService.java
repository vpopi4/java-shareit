package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.util.ClientException;

import java.util.List;

public interface ItemRequestService {
    ItemRequestShortDto createItemRequest(Integer userId,
                                          ItemRequestCreationDto dto) throws ClientException;

    List<ItemRequestDto> findAllByUserId(Integer userId) throws ClientException;

    List<ItemRequestShortDto> findAll(Integer from, Integer size) throws ClientException;

    ItemRequestDto findById(Integer requestId) throws ClientException;
}
