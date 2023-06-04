package ru.yandex.practicum.filmorate.storage.mpa;

import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;

public interface MpaRatingDao {
    List<MpaRating> findAll();

    MpaRating getMpaRatingById(int id);
}
