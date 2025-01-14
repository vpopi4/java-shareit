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
import java.util.List;

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

        return repository
                .findAllByRequesterId(userId)
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
        ItemRequest request = repository.findByIdWithItems(requestId)
                .orElseThrow(() -> new NotFoundException("request not found"));

        log.info("item request = {}", request);

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
}
