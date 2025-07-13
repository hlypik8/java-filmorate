package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.filmStorage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.reviewStorage.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.userStorage.UserStorage;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewDbStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Review addReview(Review review) {
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        return reviewStorage.addReview(review);
    }

    public Review updateReview(Review review) {
        getReviewById(review.getReviewId()); // Проверка существования
        return reviewStorage.updateReview(review);
    }

    public void deleteReview(int id) {
        reviewStorage.deleteReview(id);
    }

    public Review getReviewById(int id) {
        Review review = reviewStorage.getReviewById(id);
        if (review == null) {
            throw new NotFoundException("Отзыв не найден");
        }
        return review;
    }

    public List<Review> getReviews(Integer filmId, int count) {
        if (filmId != null) {
            filmStorage.getFilmById(filmId); // Проверка существования фильма
            return reviewStorage.getReviewsByFilmId(filmId, count);
        }
        return reviewStorage.getAllReviews(count);
    }

    public void addLike(int reviewId, int userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.addRating(reviewId, userId, true);
    }

    public void addDislike(int reviewId, int userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.addRating(reviewId, userId, false);
    }

    public void removeLike(int reviewId, int userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.removeRating(reviewId, userId);
    }

    public void removeDislike(int reviewId, int userId) {
        validateReviewAndUser(reviewId, userId);
        reviewStorage.removeRating(reviewId, userId);
    }

    private void validateUserAndFilm(int userId, int filmId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("Фильм не найден");
        }
    }

    private void validateReviewAndUser(int reviewId, int userId) {
        getReviewById(reviewId);
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден");
        }
    }
}