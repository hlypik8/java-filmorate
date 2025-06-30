package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpaStorage.MpaDbStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    public Collection<Mpa> getMpaList() {
        log.info("Запрос списка всех mpa");
        return mpaDbStorage.getMpaList();
    }

    public Mpa getMpaById(int mpaId) {
        log.info("Получение mpa с id {}", mpaId);

        Mpa mpa = mpaDbStorage.getMpaById(mpaId);

        if (mpa == null) {
            throw new MpaNotFoundException("Такого рейтинга не существует");
        }

        return mpa;
    }
}
