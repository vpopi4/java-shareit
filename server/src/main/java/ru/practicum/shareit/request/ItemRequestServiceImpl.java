package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreationDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.dto.ItemShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.ClientException;
import ru.practicum.shareit.util.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository userRepository;

    private static ItemShortDto getItemShortDto(Item item) {
        return ItemShortDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }

    @Override
    public ItemRequestShortDto createItemRequest(Integer userId,
                                                 ItemRequestCreationDto dto) throws ClientException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));

        ItemRequest.ItemRequestBuilder itemRequestBuilder = ItemRequest.builder()
                .id(null)
                .description(dto.getDescription())
                .requester(user)
                .createdAt(LocalDateTime.now());

        ItemRequest request = repository.save(itemRequestBuilder.build());

        return ItemRequestShortDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreatedAt())
                .build();
    }

    @Override
    public List<ItemRequestDto> findAllByUserId(Integer userId) throws ClientException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));

        List<ItemRequest> requests = repository.findAllByRequesterId(userId);

        return linkItems(requests)
                .stream()
                .map((request) -> ItemRequestDto.builder()
                        .id(request.getId())
                        .description(request.getDescription())
                        .created(request.getCreatedAt())
                        .items(request.getItems()
                                .stream()
                                .map(ItemRequestServiceImpl::getItemShortDto)
                                .toList())
                        .build())
                .toList();
    }

    @Override
    public List<ItemRequestShortDto> findAll(Integer from, Integer size) throws ClientException {
        return repository
                .findAllOrderByCreatedAtDesc(from, size)
                .stream()
                .map((request) -> ItemRequestShortDto.builder()
                        .id(request.getId())
                        .description(request.getDescription())
                        .created(request.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public ItemRequestDto findById(Integer requestId) throws ClientException {
        ItemRequest request = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("request not found"));

        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .created(request.getCreatedAt())
                .items(request.getItems()
                        .stream()
                        .map(ItemRequestServiceImpl::getItemShortDto)
                        .toList())
                .build();
    }

    private List<ItemRequest> linkItems(List<ItemRequest> requests) {
        List<Item> items = repository.findItemsByRequestIds(
                requests
                        .stream()
                        .map(ItemRequest::getId)
                        .toList()
        );

        Map<Integer, List<Item>> itemsByRequestId = items.stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));

        for (ItemRequest request : requests) {
            request.setItems(itemsByRequestId.getOrDefault(request.getId(), Collections.emptyList()));
        }

        return requests;
    }
}
