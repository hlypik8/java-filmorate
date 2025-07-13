package ru.yandex.practicum.filmorate.storage.reviewStorage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    void delete(int id);

    Review getById(int id);

    Collection<Review> getByFilmId(Integer filmId, int count);

    void addLike(int reviewId, int userId);

    void addDislike(int reviewId, int userId);

    void removeLike(int reviewId, int userId);

    void removeDislike(int reviewId, int userId);
}