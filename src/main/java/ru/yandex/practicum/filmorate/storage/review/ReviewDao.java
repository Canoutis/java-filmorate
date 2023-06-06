package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDao {

    Review createReview(Review review);

    Review updateReview(Review review);

    void removeReview(Integer reviewId);

    Review getReviewById(Integer reviewId);

    List<Review> findReviews(Integer filmId, Integer count);

    void addLikeToReview(Integer reviewId, Integer userId);

    void addLDislikeToReview(Integer reviewId, Integer userId);

    void removeLikeFromReview(Integer reviewId, Integer userId);

    void removeDislikeFromReview(Integer reviewId, Integer userId);
}
