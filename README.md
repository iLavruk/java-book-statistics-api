# Book Statistics API

Spring Boot сервіс для керування книгами (entity1) та авторами (entity2) з PostgreSQL, Liquibase і REST API.

## Швидкий старт
- Запуск: `mvn spring-boot:run`
- Тести (SpringBootTest + Testcontainers PostgreSQL): `mvn test`  
  Інтеграційні тести скіпаються, якщо Docker недоступний (`@Testcontainers(disabledWithoutDocker = true)`).
- Конфіг БД: `src/main/resources/application.yml` (postgres/postgres на `localhost:5432/bookstats` за замовчуванням)
- Міграції: `src/main/resources/db/changelog/db.changelog-master.yaml` (створення схеми та початкові автори)

## Основні ендпоїнти
- `POST /api/books` – створення книги (валідація обов’язкових полів)
- `GET /api/books/{id}` – деталі книги з автором
- `PUT /api/books/{id}` – оновлення книги
- `DELETE /api/books/{id}` – видалення книги
- `POST /api/books/_list` – пагінація та фільтри (authorId, yearPublished, title); відповідь `{list, totalPages}`
- `POST /api/books/_report` – CSV‑звіт за фільтрами з headers для завантаження
- `POST /api/books/upload` – імпорт JSON‑файлу формату із Завдання 1; відповідь містить imported/failed
- `GET /api/authors` – список авторів
- `POST /api/authors` – створення автора з перевіркою унікальності імені
- `PUT /api/authors/{id}` – оновлення автора з перевіркою унікальності
- `DELETE /api/authors/{id}` – видалення автора (конфлікт, якщо є книги)

## Імпорт та демо
- Файл для імпорту: `sample-data/import/books.json` (узгоджений із початковими авторами з Liquibase).
