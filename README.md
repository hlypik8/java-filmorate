Проект filmorate
---
---

Структура БД
---
![db_structure.png](db_structure.png)

GET-запросы
---
---

Запросы для фильмов
---

- Получение всех фильмов
GET(/films)
```
SELECT *
FROM films;
```

- Получение count популярных фильмов
GET(/films/popular?count={count})
```
SELECT f.*,
       COUNT(l.user_id) AS likes_count
FROM films AS f
LEFT JOIN likes AS l
ON l.film_id = f.id
GROUP BY
f.id,
f.name,
f.description,
f.release_date,
f.duration,
f.mpa_rating_id
ORDER BY likes_count DESC
LIMIT {count};
```

Запросы для пользователей
---

- Получение списка всех пользователей GET(/users)
```
SELECT *
FROM users
ORDER BY id ASC;
```
- Получение списка всех друзей GET(/users/{id}/friends)
```
SELECT friend_id
FROM friends
WHERE user_id = {id};
```
- Получение списка друзей пользователя {id}, которые пересекаются с друзьями пользователя {otherId}
GET(/users/{id}/friends/common/{otherId})
```
SELECT u.*
FROM users AS u
JOIN friends AS f1 ON u.id = f1.friend_id
JOIN friends AS f2 ON u.id = f2.friend_id
WHERE f1.user_id = ?
      AND f2.user_id = ?
      AND f1.accepted = TRUE
      AND f2.accepted = TRUE;
```