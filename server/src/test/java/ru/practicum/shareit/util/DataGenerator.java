package ru.practicum.shareit.util;

import com.github.javafaker.Faker;
import lombok.Getter;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class DataGenerator {
    @Getter
    private final Faker faker;
    private Integer seq;

    public DataGenerator() {
        faker = new Faker();
        seq = 0;
    }

    public User getUser(Integer id) {
        return User.builder()
                .id(id)
                .name(faker.name().fullName())
                .email(faker.internet().safeEmailAddress())
                .createdAt(getPastLocalDateTime())
                .build();
    }

    public Item getItem(Integer id, User owner, ItemRequest request) {
        return Item.builder()
                .id(id)
                .name(faker.commerce().productName())
                .description(getItemDescription())
                .isAvailable(true)
                .owner(owner)
                .request(request)
                .createdAt(getPastLocalDateTime())
                .build();
    }

    public ItemRequest getItemRequest(Integer id, User requester) {
        return ItemRequest.builder()
                .id(id)
                .description(getItemDescription())
                .requester(requester)
                .createdAt(getPastLocalDateTime())
                .items(Collections.emptyList())
                .build();
    }

    public Comment getComment(Integer id, Item item, User author) {
        return Comment.builder()
                .id(id)
                .text(String.join(" ", faker.lorem().sentences(3)))
                .item(item)
                .author(author)
                .createdAt(getPastLocalDateTime())
                .build();
    }

    public Booking getBooking(Integer id, Item item, User booker) {
        LocalDateTime start = getFutureLocalDateTime();
        LocalDateTime end = start.plusWeeks(1);

        return Booking.builder()
                .id(id)
                .start(start)
                .end(end)
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public String getItemDescription() {
        return String.format("%s %s %s.\n%s",
                faker.commerce().color(),
                faker.commerce().material(),
                faker.commerce().productName(),
                String.join(" ", faker.lorem().sentences(3)));
    }

    public LocalDateTime getPastLocalDateTime() {
        return faker.date().past(365, TimeUnit.DAYS)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public LocalDateTime getFutureLocalDateTime() {
        return faker.date().future(365, TimeUnit.DAYS)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public String getLorem(int words) {
        return String.join(" ", faker.lorem().words(words));
    }

    public Integer getNextId() {
        return ++seq;
    }
}
