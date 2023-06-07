package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.exception.ObjectSaveException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDao;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class DirectorService {

    private final DirectorDao directorDao;

    @Autowired
    public DirectorService(DirectorDao directorDao) {
        this.directorDao = directorDao;
    }

    public Director create(Director director) {
        Optional<Director> createdDirector = directorDao.create(director);
        if (createdDirector.isPresent()) return createdDirector.get();
        else {
            log.info("Ошибка занесения режиссера: {}", director);
            throw new ObjectSaveException(String.format("Ошибка занесения режиссера. Ошибка входных данных! %s", director));
        }
    }

    public Director update(Director director) {
        Optional<Director> createdDirector = directorDao.update(director);
        if (createdDirector.isPresent()) return createdDirector.get();
        else {
            log.info("Ошибка обновления режиссера: {}", director);
            throw new ObjectSaveException(String.format("Ошибка обновления режиссера. Ошибка входных данных! %s", director));
        }
    }

    public void removeDirectorById(int directorId) {
        directorDao.removeDirectorById(directorId);
    }

    public Director getDirectorById(int directorId) {
        Optional<Director> gotDirector = directorDao.getDirectorById(directorId);
        if (gotDirector.isPresent()) return gotDirector.get();
        else {
            log.info("Ошибка получения режиссера: {}", directorId);
            throw new ObjectNotFoundException(String.format("Ошибка получения режиссера. %x", directorId));
        }
    }

    public List<Director> findAll() {
        return directorDao.findAll();
    }
}
