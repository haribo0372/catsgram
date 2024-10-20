package ru.yandex.practicum.catsgram.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.service.PostService;
import ru.yandex.practicum.catsgram.util.enums.SortOrder;

import java.util.Collection;
import java.util.Optional;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public Collection<Post> findAll(@RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "asc") String sort,
                                    @RequestParam(defaultValue = "0") int from) {

        return postService.findAll(size, SortOrder.from(sort), from);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Post create(@RequestBody Post post) {
        return postService.create(post);
    }

    @PutMapping
    public Post update(@RequestBody Post newPost) {
        return postService.update(newPost);
    }

    @GetMapping("/{postId}")
    public Optional<Post> getPostById(@PathVariable("postId") Long postId) {
        return postService.findById(postId);
    }

}