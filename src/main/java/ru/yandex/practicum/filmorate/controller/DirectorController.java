package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
public class DirectorController {

    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping(value = "/directors")
    public Director create(@Valid @RequestBody Director director) {
        return directorService.create(director);
    }

    @PutMapping(value = "/directors")
    public Director update(@Valid @RequestBody Director director) {
        return directorService.update(director);
    }

    @GetMapping(value = "/directors")
    public List<Director> findAll() {
        return directorService.findAll();
    }

    @GetMapping(value = "/directors/{id}")
    public Director getDirectorById(@PathVariable int id) {
        return directorService.getDirectorById(id);
    }

    @DeleteMapping(value = "/directors/{id}")
    public void removeDirectorById(@PathVariable int id) {
        directorService.removeDirectorById(id);
    }

}
