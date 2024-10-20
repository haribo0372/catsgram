package ru.yandex.practicum.catsgram.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.exception.ParameterNotValidException;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.util.enums.SortOrder;

import java.time.Instant;
import java.util.*;

@Service
public class PostService {
    private final Map<Long, Post> posts = new HashMap<>();
    private final UserService userService;

    @Autowired
    public PostService(UserService userService) {
        this.userService = userService;
    }

    public Collection<Post> findAll() {
        return posts.values();
    }

    public Collection<Post> findAll(int size, SortOrder sort, int from) {
        Comparator<Post> dataComparator;

        switch (sort) {
            case ASCENDING -> dataComparator = Comparator.comparing(Post::getPostDate);
            case DESCENDING -> dataComparator = (o1, o2) -> -1 * o1.getPostDate().compareTo(o2.getPostDate());
            default -> throw new ConditionsNotMetException("Данный режим сортировки не поддерживается");
        }

        if (size <= 0)
            throw new ParameterNotValidException("size", "Некорректный размер выборки. Размер должен быть больше нуля");

        if (from < 0)
            throw new ParameterNotValidException("from", "Некорректный индекс от. Индекс от должен быть не меньше нуля");

        return posts.values().stream()
                .sorted(dataComparator)
                .skip(from)
                .limit(size).toList();
    }

    public Optional<Post> findById(Long postId) {
        return posts.values().stream()
                .filter(i -> Objects.equals(postId, i.getId())).findAny();
    }

    public Post create(Post post) {
        if (post.getDescription() == null || post.getDescription().isBlank())
            throw new ConditionsNotMetException("Описание не может быть пустым");

        if (userService.findById(post.getId()).isEmpty())
            throw new ConditionsNotMetException(String.format("«Автор с id = %s не найден»", post.getId()));

        post.setId(getNextId());
        post.setPostDate(Instant.now());
        posts.put(post.getId(), post);
        return post;
    }

    public Post update(Post newPost) {
        if (newPost.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (posts.containsKey(newPost.getId())) {
            Post oldPost = posts.get(newPost.getId());
            if (newPost.getDescription() == null || newPost.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым");
            }
            oldPost.setDescription(newPost.getDescription());
            return oldPost;
        }
        throw new NotFoundException("Пост с id = " + newPost.getId() + " не найден");
    }

    private long getNextId() {
        long currentMaxId = posts.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
