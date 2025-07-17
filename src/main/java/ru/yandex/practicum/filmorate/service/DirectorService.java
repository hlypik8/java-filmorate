package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.InvalidRequestFormat;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.directorStorage.DirectorDbStorage;

import java.util.Collection;

@Slf4j
@Service
@AllArgsConstructor
public class DirectorService {

    private final DirectorDbStorage directorStorage;

    public Collection<Director> getDirectorList() {
        log.info("Запрос списка всех директоров");
        return directorStorage.getDirectorsList();
    }

    public Director getDirectorById(int directorId) {
        log.info("Запрос режиссера с id {}", directorId);
        return directorStorage.getDirectorById(directorId);
    }

    public Director newDirector(Director director) {
        log.info("Добавление нового режиссера");
        validateDirector(director);
        return directorStorage.newDirector(director);
    }

    public Director updateDirector(Director director) {
        log.info("Обновление режиссера с id {}", director.getId());
        validateDirector(director);
        return directorStorage.updateDirector(director);
    }

    public void deleteDirector(int id) {
        log.info("Удаление режиссера с id {}", id);
        directorStorage.deleteDirector(id);
        log.info("Удаление режиссера с id {} прошло успешно", id);
    }

    private void validateDirector(Director director) {
        if (director.getName().isBlank() || director.getName().isEmpty()) {
            throw new InvalidRequestFormat("Режиссер не может быть без имени");
        }
    }

}
