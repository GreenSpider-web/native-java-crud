# native-java-crud

Сайт для завантаження гри, написаний на чистій Java без фреймворків: власний HTTP-сервер, JDBC і PostgreSQL.

> Проєкт зараз перебудовується. Тут описано **цільову** архітектуру. Частина коду ще відповідає старій версії (форма реєстрації користувачів).

## Сторінки

| Шлях | Метод | Сторінка |
|---|---|---|
| `/` | GET | Головна: опис гри і кнопка завантаження |
| `/download` | GET | Віддає файл гри та збільшує лічильник завантажень |
| `/about` | GET | Про нас: команда, історія проєкту |
| `/support` | GET | Підтримка: FAQ і форма звернення |
| `/support` | POST | Приймає звернення, зберігає його в БД і перенаправляє (303) на `/support?sent=true` |
| `/register` | GET | Реєстрація: форма створення акаунта (*необов'язкова*) |
| `/register` | POST | Перевіряє дані, зберігає користувача з хешованим паролем і перенаправляє (303) на `/register?success=true` |
| `/static/*` | GET | CSS, зображення, скрипти |
| будь-що інше | — | 404 |

Реєстрація **необов'язкова**: гру можна завантажити й написати в підтримку без акаунта.

## Технології

- **Java 26**, без фреймворків
- **PostgreSQL** + драйвер JDBC `org.postgresql:postgresql`
- **Maven** для збирання

## Архітектура

Застосунок розділений на шари. Кожен шар звертається лише до шару під ним:

```
          HTTP-запит
              │
              ▼
┌───────────────────────────┐
│  server                   │  приймає сокет, парсить HTTP у HttpRequest,
│  (HttpServer, Router)     │  за шляхом і методом знаходить потрібний контролер
└─────────────┬─────────────┘
              ▼
┌───────────────────────────┐
│  controller               │  читає параметри запиту, викликає сервіс,
│                           │  вибирає view і код відповіді (200/303/400/404/500)
└─────────────┬─────────────┘
              ▼
┌───────────────────────────┐
│  service                  │  бізнес-логіка і валідація
│                           │  (наприклад, перевірка email у зверненні)
└─────────────┬─────────────┘
              ▼
┌───────────────────────────┐
│  repository               │  SQL через PreparedStatement,
│  (інтерфейс + Jdbc-реаліз.)│  мапінг ResultSet → модель
└─────────────┬─────────────┘
              ▼
┌───────────────────────────┐
│  db (ConnectionPool)      │  фіксований пул з'єднань, відкритих один раз
└─────────────┬─────────────┘
              ▼
          PostgreSQL
```

Правила:

- **Контролери не пишуть SQL**, а репозиторії нічого не знають про HTTP.
- **Сервіси й репозиторії описані інтерфейсами**, щоб у тестах їх можна було підмінити.
- **Залежності створюються в одному місці** (`Main`) і передаються через конструктори, без статичних методів у DAO.
- **Помилки валідації дають 400** з повідомленням для користувача. Непередбачені помилки дають 500 і пишуться в лог.
- **Дані користувача в HTML завжди екрануються** (`HtmlEscaper`), щоб не було XSS.
- **HTTP-запит читається повністю**: спочатку заголовки, потім тіло рівно за `Content-Length`.
- **Після POST відповідь — редирект 303** (Post/Redirect/Get), щоб F5 не надсилав форму вдруге.
- **Паролі ніколи не зберігаються у відкритому вигляді**: у БД лежить лише хеш PBKDF2 із сіллю (`javax.crypto`, без сторонніх бібліотек).

## Структура пакетів

```
src/main/java/by/spider/
├── Main.java                     # точка входу, збирає всі залежності
├── config/
│   └── AppConfig.java            # читає application.properties та змінні середовища
├── server/
│   ├── HttpServer.java           # ServerSocket + пул потоків
│   ├── HttpRequest.java          # метод, шлях, заголовки, параметри, тіло
│   ├── HttpResponse.java         # статус, заголовки, тіло; запис у сокет
│   ├── HttpStatus.java           # enum кодів відповіді
│   ├── Router.java               # (метод, шлях) → Handler
│   ├── Handler.java              # функціональний інтерфейс обробника
│   └── StaticFileHandler.java    # віддача /static/* і файлу гри
├── controller/
│   ├── HomeController.java       # /, /download
│   ├── AboutController.java      # /about
│   ├── SupportController.java    # /support (GET, POST)
│   └── RegistrationController.java # /register (GET, POST)
├── service/
│   ├── DownloadService.java
│   ├── SupportService.java
│   ├── UserService.java          # реєстрація, перевірка унікальності email
│   ├── PasswordHasher.java       # PBKDF2 + сіль
│   └── ValidationException.java  # перетворюється на відповідь 400
├── repository/
│   ├── DownloadRepository.java
│   ├── JdbcDownloadRepository.java
│   ├── SupportTicketRepository.java
│   ├── JdbcSupportTicketRepository.java
│   ├── UserRepository.java
│   └── JdbcUserRepository.java
├── db/
│   └── ConnectionPool.java       # BlockingQueue<Connection>
├── model/
│   ├── SupportTicket.java
│   ├── GameRelease.java
│   └── User.java
└── view/
    ├── TemplateEngine.java       # підставляє значення в HTML-шаблони
    └── HtmlEscaper.java

src/main/resources/
├── application.properties.example
├── db/
│   └── schema.sql                # створення таблиць
├── templates/                    # HTML-шаблони сторінок
│   ├── layout.html               # спільні header/footer і навігація
│   ├── home.html
│   ├── about.html
│   ├── support.html
│   ├── register.html
│   └── 404.html
└── static/
    ├── css/style.css
    └── img/

src/test/java/by/spider/          # JUnit-тести сервісів і парсера HTTP
```

## База даних

```sql
CREATE SCHEMA IF NOT EXISTS game_site;

-- Версії гри, які можна завантажити
CREATE TABLE game_site.releases (
    id           SERIAL PRIMARY KEY,
    version      VARCHAR(32)  NOT NULL UNIQUE,
    file_name    VARCHAR(255) NOT NULL,
    released_at  TIMESTAMP    NOT NULL DEFAULT now(),
    downloads    INTEGER      NOT NULL DEFAULT 0
);

-- Звернення в підтримку
CREATE TABLE game_site.support_tickets (
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(100)  NOT NULL,
    email       VARCHAR(255)  NOT NULL,
    message     TEXT          NOT NULL,
    created_at  TIMESTAMP     NOT NULL DEFAULT now()
);

-- Зареєстровані користувачі (реєстрація необов'язкова)
CREATE TABLE game_site.users (
    id             SERIAL PRIMARY KEY,
    username       VARCHAR(50)   NOT NULL UNIQUE,
    email          VARCHAR(255)  NOT NULL UNIQUE,
    password_hash  VARCHAR(255)  NOT NULL,
    created_at     TIMESTAMP     NOT NULL DEFAULT now()
);
```

## Запуск

1. Встановіть JDK 26, Maven і PostgreSQL.
2. Створіть таблиці зі скрипта `src/main/resources/db/schema.sql`.
3. Скопіюйте конфіг і впишіть свої дані:
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```
   Файл `application.properties` є в `.gitignore`, тому **не комітьте його**.
4. Зберіть і запустіть:
   ```bash
   mvn package
   java -cp target/classes:<шлях до postgresql.jar> by.spider.Main
   ```
   Або просто запустіть `Main` з IntelliJ IDEA.
5. Відкрийте http://localhost:8080
