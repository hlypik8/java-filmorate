merge into genres (id, name) values (1, 'Комедия');
merge into genres (id, name) values (2, 'Драма');
merge into genres (id, name) values (3, 'Мультфильм');
merge into genres (id, name) values (4, 'Триллер');
merge into genres (id, name) values (5, 'Документальный');
merge into genres (id, name) values (6, 'Боевик');

merge into mpa_ratings (id, name, description) values (1, 'G', 'Нет возрастных ограничений');
merge into mpa_ratings (id, name, description) values (2, 'PG', 'Рекомендуется присутствие родителей');
merge into mpa_ratings (id, name, description) values (3, 'PG-13', 'Детям до 13 лет просмотр не желателен');
merge into mpa_ratings (id, name, description) values (4, 'R','Лицам до 17 лет обязательно присутствие взрослого');
merge into mpa_ratings (id, name, description) values (5, 'NC-17', 'Лицам до 18 лет просмотр запрещен');