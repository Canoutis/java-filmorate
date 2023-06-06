package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewDaoImpl;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDaoImpl reviewDao;

    public Review createReview(Review review) {
        return null;
    }

    public Review updateReview(Review review) {
        return null;
    }

    public void removeReview(Integer reviewId) {
    }

    public Review findReview(Integer reviewId){
        return null;
    }

    public List<Review> findReviews(Integer filmId, Integer limit) {
        return null;
    }

    public void addLikeToReview(Integer reviewId, Integer userId) {

    }

    public void addLDislikeToReview(Integer reviewId, Integer userId) {

    }

    public void removeLikeFromReview(Integer reviewId, Integer userId) {

    }

    public void removeDislikeFromReview(Integer reviewId, Integer userId) {

    }
}
