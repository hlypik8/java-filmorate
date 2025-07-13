package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.reviewStorage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Review create(Review review) {
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        Review created = reviewStorage.create(review);
        log.info("Создан новый отзыв: {}", created);
        return created;
    }

    public Review update(Review review) {
        getById(review.getReviewId()); // Проверка существования
        Review updated = reviewStorage.update(review);
        log.info("Обновлен отзыв: {}", updated);
        return updated;
    }

    public void delete(int id) {
        reviewStorage.delete(id);
        log.info("Удален отзыв с id={}", id);
    }

    public Review getById(int id) {
        Review review = reviewStorage.getById(id);
        log.info("Получен отзыв: {}", review);
        return review;
    }

    public Collection<Review> getByFilmId(Integer filmId, int count) {
        if (filmId != null) {
            filmStorage.getFilmById(filmId); // Проверка существования фильма
        }
        Collection<Review> reviews = reviewStorage.getByFilmId(filmId, count);
        log.info("Получено {} отзывов для фильма {}", reviews.size(), filmId);
        return reviews;
    }

    public void addLike(int reviewId, int userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.addLike(reviewId, userId);
        log.info("Пользователь {} поставил лайк отзыву {}", userId, reviewId);
    }

    public void addDislike(int reviewId, int userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.addDislike(reviewId, userId);
        log.info("Пользователь {} поставил дизлайк отзыву {}", userId, reviewId);
    }

    public void removeLike(int reviewId, int userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.removeLike(reviewId, userId);
        log.info("Пользователь {} удалил лайк отзыву {}", userId, reviewId);
    }

    public void removeDislike(int reviewId, int userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.removeDislike(reviewId, userId);
        log.info("Пользователь {} удалил дизлайк отзыву {}", userId, reviewId);
    }

    private void validateUserAndFilm(int userId, int filmId) {
        userStorage.getUserById(userId);
        filmStorage.getFilmById(filmId);
    }

    private void validateReviewAndUser(int reviewId, int userId) {
        getById(reviewId);
        userStorage.getUserById(userId);
    }
}