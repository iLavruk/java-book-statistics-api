# Book Statistics API

Spring Boot сервіс для керування книгами (entity1) та авторами (entity2) з PostgreSQL, Liquibase та REST API.

## Швидкий старт
- Запуск: `mvn spring-boot:run`
- Тести (SpringBootTest + Testcontainers PostgreSQL): `mvn test`
- Примітка щодо тестів: інтеграційні тести піднімають PostgreSQL через Testcontainers і автоматично скіпаються, якщо Docker недоступний (анотація `@Testcontainers(disabledWithoutDocker = true)`). Для повного прогону запустіть Docker і повторіть `mvn test`.
- Конфіг БД: `src/main/resources/application.yml` (за замовчуванням postgres/postgres на localhost:5432/bookstats)
- Міграції: `src/main/resources/db/changelog/db.changelog-master.yaml` (створення схем та початкові автори)

## Основні ендпоїнти
- `POST /api/books` – створення книги (валідовано обов’язкові поля)
- `GET /api/books/{id}` – детальні дані книги з автором
- `PUT /api/books/{id}` – оновлення книги
- `DELETE /api/books/{id}` – видалення книги
- `POST /api/books/_list` – пагінація та фільтри (authorId, yearPublished, title); відповідає `{list, totalPages}`
- `POST /api/books/_report` – CSV-репорт за фільтрами з правильними headers для завантаження
- `POST /api/books/upload` – завантаження JSON-файлу у форматі з Завдання 1; повертає кількість імпортованих/помилкових
- `GET /api/authors` – список авторів
- `POST /api/authors` – створення автора з перевіркою унікальності імені
- `PUT /api/authors/{id}` – оновлення автора з перевіркою унікальності
- `DELETE /api/authors/{id}` – видалення автора (конфлікт, якщо є книги)

## Імпорт та демодані
- Файл для імпорту: `sample-data/import/books.json` (узгоджений із початковими авторами з Liquibase).
