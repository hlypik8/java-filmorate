package ru.yandex.practicum.filmorate.controllers;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@AllArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> findAll() {
        return directorService.getDirectorList();
    }

    @GetMapping("/{id}")
    public Director findOne(@PathVariable int id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public Director post(@Valid @RequestBody Director director) {
        return directorService.newDirector(director);
    }

    @PutMapping
    public Director put(@RequestBody Director director) {
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        directorService.deleteDirector(id);
    }

}
