package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.BadRequestException;
import ru.practicum.shareit.util.ClientException;
import ru.practicum.shareit.util.ForbiddenException;
import ru.practicum.shareit.util.NotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper map;
    private final BookingMapper bookingMapper;

    @Override
    public ItemDto.Response.PublicInfo createItem(Integer userId, ItemDto.Request.Create dto) throws ClientException {
        User user = findUser(userId);

        Item item = map.toItem(dto, user);

        repository.save(item);

        return map.toDto(item);
    }

    @Override
    public ItemDto.Response.PublicInfo updatePartially(Integer userId,
                                                       Integer itemId,
                                                       ItemDto.Request.UpdatePartially dto) throws ClientException {
        User user = findUser(userId);
        Item item = findItem(itemId);

        if (!Objects.equals(user.getId(), item.getOwner().getId())) {
            throw new ForbiddenException("editing denied");
        }

        if (dto.getName() != null) {
            item.setName(dto.getName());
        }

        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }

        if (dto.getAvailable() != null) {
            item.setIsAvailable(dto.getAvailable());
        }

        repository.save(item);

        return map.toDto(item);
    }

    @Override
    public ItemDto.Response.PublicInfo getById(Integer itemId) throws ClientException {
        Item item = repository.findByIdWithComments(itemId)
                .orElseThrow(() -> new NotFoundException("item not found"));

        return map.toDto(item);
    }

    @Override
    public List<ItemDto.Response.PrivateInfo> getAllByUserId(Integer userId) {
        List<Item> items = repository.findByOwnerId(userId);

        return items.stream().map(item -> {
            Booking lastBooking = bookingRepository.findLatestBooking(item.getId());
            Booking nextBooking = bookingRepository.findNextBooking(item.getId());

            BookingDto lastBookingDto = lastBooking != null
                    ? bookingMapper.toBookingDto(lastBooking)
                    : null;
            BookingDto nextBookingDto = nextBooking != null
                    ? bookingMapper.toBookingDto(nextBooking)
                    : null;

            return map.toPrivateDto(item, lastBookingDto, nextBookingDto);
        }).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto.Response.PublicInfo> search(String text) {
        if (text != null && text.isBlank()) {
            return Collections.emptyList();
        }

        return repository
                .findByText(text)
                .stream()
                .map(map::toDto)
                .toList();
    }

    @Override
    public CommentDto postComment(Integer userId,
                                  Integer itemId,
                                  CommentCreationDto dto) throws ClientException {
        User user = findUser(userId);
        Item item = findItem(itemId);
        List<Booking> bookings = bookingRepository.findPastBookingsByBookerAndItem(userId, itemId);

        if (bookings.isEmpty()) {
            throw new BadRequestException("commenting is not available");
        }

        Comment comment = commentRepository.save(
                Comment.builder()
                        .id(null)
                        .text(dto.getText())
                        .author(user)
                        .item(item)
                        .createdAt(LocalDateTime.now())
                        .build()
        );

        return map.toCommentDto(comment);
    }

    private User findUser(Integer userId) throws NotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("user not found"));
    }

    private Item findItem(Integer itemId) throws NotFoundException {
        return repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("item not found"));
    }
}
