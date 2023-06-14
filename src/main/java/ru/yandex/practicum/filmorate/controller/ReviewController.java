package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{reviewId}")
    public void removeReview(@PathVariable Integer reviewId) {
        reviewService.removeReview(reviewId);
    }

    @GetMapping("/{reviewId}")
    public Review getReviewById(@PathVariable Integer reviewId) {
        return reviewService.getReviewById(reviewId);
    }

    @GetMapping()
    public List<Review> findReviews(@RequestParam(required = false) Integer filmId, @RequestParam(required = false, defaultValue = "10") Integer count) {
        return reviewService.findReviews(filmId, count);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public void addLikeToReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.addLikeToReview(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addLDislikeToReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.addLDislikeToReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void removeLikeFromReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.removeLikeFromReview(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void removeDislikeFromReview(@PathVariable Integer reviewId, @PathVariable Integer userId) {
        reviewService.removeDislikeFromReview(reviewId, userId);
    }
}
