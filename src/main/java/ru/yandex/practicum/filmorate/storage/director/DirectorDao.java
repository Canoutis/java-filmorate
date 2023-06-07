package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorDao {
    List<Director> findAll();

    Optional<Director> getDirectorById(int id);

    Optional<Director> create(Director director);

    Optional<Director> update(Director director);

    void removeDirectorById(int id);
}
