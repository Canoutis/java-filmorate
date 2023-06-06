package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{reviewId}")
    public void removeReview(Integer reviewId) {
        reviewService.removeReview(reviewId);
    }

    @GetMapping("/{reviewId}")
    public Review findReview(Integer reviewId) {
        return reviewService.findReview(reviewId);
    }

    @GetMapping()
    public List<Review> findReviews(@RequestParam(required = false) Integer filmId, @RequestParam(required = false, defaultValue = "10") Integer limit) {
        return reviewService.findReviews(filmId, limit);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public void addLikeToReview(Integer reviewId, Integer userId) {
        reviewService.addLikeToReview(reviewId, userId);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public void addLDislikeToReview(Integer reviewId, Integer userId) {
        reviewService.addLDislikeToReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void removeLikeFromReview(Integer reviewId, Integer userId) {
        reviewService.removeLikeFromReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void removeDislikeFromReview(Integer reviewId, Integer userId) {
        reviewService.removeDislikeFromReview(reviewId, userId);
    }
}
