package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.review.ReviewDaoImpl;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDaoImpl reviewDao;
}
