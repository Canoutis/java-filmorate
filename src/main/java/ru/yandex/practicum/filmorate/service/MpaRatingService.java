package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.mpa.MpaRatingDaoImpl;

import java.util.List;

@Service
public class MpaRatingService {

    private final MpaRatingDaoImpl mpaRatingDaoImpl;

    @Autowired
    public MpaRatingService(MpaRatingDaoImpl mpaRatingDaoImpl) {
        this.mpaRatingDaoImpl = mpaRatingDaoImpl;
    }

    public List<MpaRating> findAll() {
        return mpaRatingDaoImpl.findAll();
    }

    public MpaRating getMpaRatingById(int id) {
        return mpaRatingDaoImpl.getMpaRatingById(id);
    }
}
