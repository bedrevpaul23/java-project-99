# Task Manager

[![Hexlet Check](https://github.com/bedrevpaul23/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/bedrevpaul23/java-project-99/actions/workflows/hexlet-check.yml)
[![Build](https://github.com/bedrevpaul23/java-project-99/actions/workflows/build.yml/badge.svg)](https://github.com/bedrevpaul23/java-project-99/actions/workflows/build.yml)

[Русский](#русский) · [English](#english)

## Статус проекта / Project status

**Последнее обновление / Last update: 24 ноября 2026 / November 24, 2026**

Проект активно поддерживается. В последнем обновлении:

- JWT и данные текущего пользователя перенесены из постоянного хранилища в `sessionStorage`, пароль больше не сохраняется в браузере, а выход полностью очищает сессию;
- API пользователей не возвращает пароли или их хеши; изменение и удаление чужой учётной записи блокируется ответом `403 Forbidden`, при этом просмотр профиля оставлен доступным для корректной работы интерфейса;
- добавлена Content Security Policy, отключена отправка персональных данных в Sentry, Docker-контейнер запускается от непривилегированного пользователя;
- восстановлена совместная работа фильтров задач и зафиксирована воспроизводимая сборка frontend;
- CI теперь пересобирает и проверяет опубликованный frontend, запускает Java-тесты, Spotless и сборку JAR; `Build` и `Hexlet Check` проходят успешно.

The project is actively maintained. The latest update:

- moved the JWT and current-user data from persistent storage to `sessionStorage`, stopped storing the password in the browser, and made sign-out clear the complete session;
- ensured the user API never returns passwords or password hashes; attempts to update or delete another account receive `403 Forbidden`, while profile reads remain available for the UI flow;
- added a Content Security Policy, disabled PII reporting to Sentry, and made the Docker container run as an unprivileged user;
- restored combined task filters and pinned a reproducible frontend build;
- made CI rebuild and validate the published frontend, run Java tests and Spotless, and build the JAR; both `Build` and `Hexlet Check` pass.

# Русский

## О проекте

Task Manager — веб-приложение для управления задачами, пользователями, статусами задач и метками. В приложении есть браузерный интерфейс и REST API. Защищённые операции требуют JWT-аутентификации.

## Демо

Приложение доступно по адресу [java-project-99-frxd.onrender.com](https://java-project-99-frxd.onrender.com).

Приложение размещено на Render. После периода неактивности первый запуск может занять некоторое время.

Доступ к Render и размещённому приложению может быть ограничен из некоторых сетей, в том числе из российского интернета. Если сайт не открывается или соединение завершается по таймауту, попробуйте использовать VPN.

Для входа можно использовать заранее созданную учебную тестовую учётную запись:

- Электронная почта: `hexlet@example.com`
- Пароль: `qwerty`

## Возможности

- JWT-аутентификация
- Управление пользователями
- Управление статусами задач
- Управление метками
- Создание, просмотр, редактирование и удаление задач (CRUD)
- Назначение исполнителя задачи
- Добавление меток к задачам
- Фильтрация задач по названию, исполнителю, статусу и метке
- Документация API с помощью Swagger/OpenAPI
- PostgreSQL в рабочем окружении
- H2 для локальной разработки и тестирования
- Отслеживание ошибок с помощью Sentry
- Развёртывание с помощью Docker

## Как пользоваться

После входа в приложение становятся доступны основные разделы для работы с задачами и связанными сущностями.

### Задачи

Раздел `Tasks` содержит список существующих задач. Кнопка `Create` создаёт новую задачу, `Edit` открывает её редактирование, а `Show` — страницу с подробной информацией.

При создании или редактировании задачи можно:

- указать название и описание;
- выбрать статус в поле `Status`;
- назначить исполнителя в поле `Assignee`;
- добавить метки в поле `Labels`.

В верхней части списка задач доступны фильтры:

- `Title` — поиск по части названия задачи;
- `Assignee` — фильтрация по назначенному исполнителю;
- `Status` — фильтрация по статусу;
- `Label` — фильтрация по метке.

Несколько фильтров можно применять одновременно.

### Пользователи

В разделе пользователей можно просматривать список и профили пользователей, а также создавать новые учётные записи. Пароли и их хеши никогда не возвращаются API. Пользователь может редактировать и удалять только собственную учётную запись. Удаление невозможно, если пользователь назначен исполнителем существующей задачи.

### Статусы задач

Статусы определяют текущее состояние задачи. Их можно создавать, редактировать и удалять. Статус нельзя удалить, пока он используется существующей задачей.

При первом запуске приложения создаются следующие статусы:

- `Draft`
- `ToReview`
- `ToBeFixed`
- `ToPublish`
- `Published`

### Метки

Метки используются для классификации задач. Их можно создавать, редактировать и удалять, а затем назначать задачам. Метку нельзя удалить, пока она используется существующей задачей.

При первом запуске приложения создаются следующие метки:

- `feature`
- `bug`

### Аутентификация

Защищённые разделы приложения требуют входа. После успешной аутентификации веб-интерфейс получает JWT через `POST /api/login` и использует его для обращения к защищённым API.

JWT и данные текущего пользователя хранятся только в `sessionStorage` текущей вкладки и удаляются при выходе или закрытии вкладки. В `localStorage` остаётся только несекретный признак активной сессии. Сервер дополнительно проверяет владельца при изменении и удалении пользователя, а браузерные ответы защищены политикой Content Security Policy.

## API

Интерактивная документация REST API доступна здесь:

- [Swagger UI — интерактивная документация API](https://java-project-99-frxd.onrender.com/swagger-ui/index.html)
- [OpenAPI JSON — спецификация API](https://java-project-99-frxd.onrender.com/v3/api-docs)

Основные группы API:

- `POST /api/login`
- `/api/users`
- `/api/task_statuses`
- `/api/labels`
- `/api/tasks`

Для защищённых API-запросов требуется Bearer JWT. Эндпоинт `POST /api/login` доступен без предварительной аутентификации.

Полный список операций, параметры запросов и схемы данных можно посмотреть в Swagger UI.

## Технологии

- Java 21
- Spring Boot 3.4.5
- Spring MVC
- Spring Data JPA / Hibernate
- Spring Security
- JWT / OAuth2 Resource Server
- PostgreSQL
- H2
- Gradle 9.6
- Springdoc OpenAPI / Swagger UI
- Sentry
- Docker
- GitHub Actions
- Render
- Hexlet Task Manager frontend

## Локальный запуск

Для локального запуска требуется JDK 21. Node.js 24 нужен только для пересборки браузерного интерфейса.

Клонируйте репозиторий и запустите приложение:

```bash
git clone https://github.com/bedrevpaul23/java-project-99.git
cd java-project-99
./gradlew bootRun
```

По умолчанию используется профиль `dev` и база данных H2, работающая в памяти.

После запуска приложение будет доступно по адресу:

[http://localhost:8080](http://localhost:8080)

Для входа можно использовать тестовую учётную запись:

- Электронная почта: `hexlet@example.com`
- Пароль: `qwerty`

Альтернативный запуск через Makefile:

```bash
make setup
make start
```

`make setup` собирает приложение, а `make start` запускает собранный JAR.

## Пересборка frontend

Готовый frontend хранится в `src/main/resources/static/` и публикуется вместе со Spring Boot-приложением. После изменения версии frontend или защитных преобразований пересоберите и проверьте bundle:

```bash
npm ci --ignore-scripts --no-audit --no-fund
node scripts/build-secure-frontend.mjs
node scripts/check-frontend.mjs
```

Скрипт сборки применяет проектные исправления к пакету `@hexlet/java-task-manager-frontend`, создаёт новый bundle и обновляет ссылку на него в `index.html`. Проверка предотвращает возврат хранения JWT, данных пользователя или пароля в постоянное хранилище и контролирует совместимость фильтров и списка пользователей.

## Тесты и проверки

Каждый push проверяется двумя workflow GitHub Actions:

- `Build` воспроизводимо собирает опубликованный frontend, проверяет отсутствие JWT, данных пользователя и пароля в постоянном хранилище браузера, запускает Java-тесты, Spotless и сборку исполняемого JAR;
- `Hexlet Check` запускает сквозные браузерные сценарии для аутентификации, пользователей, статусов, меток, задач и фильтрации.

Для запуска автоматических проверок:

```bash
./gradlew clean check
```

Для проверки форматирования Java-кода:

```bash
./gradlew spotlessCheck
```

Для сборки исполняемого Spring Boot JAR:

```bash
./gradlew bootJar
```

## Настройка рабочего окружения

При развёртывании приложения необходимо настроить следующие переменные окружения:

| Переменная | Назначение |
| --- | --- |
| `PORT` | HTTP-порт приложения. По умолчанию используется `8080`. |
| `SPRING_PROFILES_ACTIVE` | Активный профиль Spring. Для рабочего окружения используется значение `prod`. |
| `DB_HOST` | Адрес сервера PostgreSQL. |
| `DB_PORT` | Порт PostgreSQL. По умолчанию используется `5432`. |
| `DB_NAME` | Имя базы данных PostgreSQL. |
| `DB_USER` | Пользователь PostgreSQL. |
| `DB_PASSWORD` | Пароль пользователя PostgreSQL. |
| `SENTRY_DSN` | Используется приложением для отправки событий об ошибках в Sentry. |
| `SENTRY_AUTH_TOKEN` | Используется инструментами Sentry при сборке и загрузке контекста исходного кода. |
| `SENTRY_ORG` | Организация Sentry, используемая инструментами загрузки контекста исходного кода. |
| `SENTRY_PROJECT` | Проект Sentry, используемый инструментами загрузки контекста исходного кода. |

Для рабочего окружения необходимо установить:

```text
SPRING_PROFILES_ACTIVE=prod
```

Секретные значения передаются приложению через переменные окружения и не хранятся в репозитории.

## Мониторинг ошибок

Ошибки приложения в рабочем окружении отправляются в Sentry. Это позволяет отслеживать исключения и получать информацию, необходимую для их диагностики.

Параметры подключения к Sentry задаются через переменные окружения. Токены, DSN и другие секретные значения в исходном коде не хранятся.

## Структура проекта

Основной код серверной части расположен в `src/main/java/hexlet/code/`:

```text
src/main/java/hexlet/code/
  component/
  config/
  controller/
  dto/
  exception/
  mapper/
  model/
  repository/
  security/
  service/
  specification/
```

Ресурсы и конфигурация приложения расположены в `src/main/resources/`:

```text
src/main/resources/
  static/
  application*.yml
```

Скрипты сборки frontend находятся в `scripts/`, а конфигурация CI — в `.github/workflows/`.

---

# English

## About

Task Manager is a web application for managing tasks, users, task statuses, and labels. It includes a browser UI and a REST API. Protected operations require JWT authentication.

## Live demo

The application is available at [java-project-99-frxd.onrender.com](https://java-project-99-frxd.onrender.com).

The application is hosted on Render. After a period of inactivity, the first start may take some time.

Access to Render and the deployed application may be restricted from some networks, including networks in Russia. If the site does not open or the connection times out, try using a VPN.

Use the seeded educational demo account:

- Email: `hexlet@example.com`
- Password: `qwerty`

## Features

- JWT authentication
- User management
- Task status management
- Label management
- Task CRUD
- Assigning tasks to users
- Assigning labels to tasks
- Task filtering by title, assignee, status, and label
- Swagger/OpenAPI documentation
- PostgreSQL in production
- H2 for local development and testing
- Sentry error tracking
- Docker deployment

## How to use

After signing in, the browser UI provides the main sections for working with tasks and related entities.

### Tasks

The `Tasks` section contains the list of existing tasks. `Create` adds a new task, `Edit` opens it for editing, and `Show` opens a page with detailed information.

When creating or editing a task, you can:

- provide a title and description;
- choose a status in the `Status` field;
- assign a user in the `Assignee` field;
- add labels in the `Labels` field.

Filters are available at the top of the task list:

- `Title` — searches for part of a task title;
- `Assignee` — filters by the assigned user;
- `Status` — filters by task status;
- `Label` — filters by label.

Multiple filters can be applied at the same time.

### Users

The Users section lets you view the user directory and individual profiles, and create new accounts. Passwords and password hashes are never returned by the API. A user can edit and delete only their own account. An account cannot be deleted while that user is assigned to an existing task.

### Task statuses

Task statuses define the current state of a task. They can be created, edited, and deleted. A status cannot be deleted while it is used by an existing task.

The following statuses are created when the application starts:

- `Draft`
- `ToReview`
- `ToBeFixed`
- `ToPublish`
- `Published`

### Labels

Labels are used to classify tasks. They can be created, edited, and deleted, and then assigned to tasks. A label cannot be deleted while it is used by an existing task.

The following labels are created when the application starts:

- `feature`
- `bug`

### Authentication

Protected sections require sign-in. After successful authentication, the browser UI receives a JWT through `POST /api/login` and uses it to access protected APIs.

The JWT and current-user data are kept only in the current tab's `sessionStorage` and are removed on sign-out or when the tab is closed. Only a non-secret active-session marker remains in `localStorage`. The server also verifies ownership when a user account is updated or deleted, and browser responses are protected by a Content Security Policy.

## API documentation

Interactive REST API documentation is available here:

- [Swagger UI — interactive API documentation](https://java-project-99-frxd.onrender.com/swagger-ui/index.html)
- [OpenAPI JSON — API specification](https://java-project-99-frxd.onrender.com/v3/api-docs)

The main API groups are:

- `POST /api/login`
- `/api/users`
- `/api/task_statuses`
- `/api/labels`
- `/api/tasks`

Protected API endpoints require a Bearer JWT. `POST /api/login` is available without prior authentication.

Swagger UI contains the full list of operations, request parameters, and data schemas.

## Tech stack

- Java 21
- Spring Boot 3.4.5
- Spring MVC
- Spring Data JPA / Hibernate
- Spring Security
- JWT / OAuth2 Resource Server
- PostgreSQL
- H2
- Gradle 9.6
- Springdoc OpenAPI / Swagger UI
- Sentry
- Docker
- GitHub Actions
- Render
- Hexlet Task Manager frontend

## Local development

JDK 21 is required. Node.js 24 is needed only when rebuilding the browser UI.

Clone the repository and start the application:

```bash
git clone https://github.com/bedrevpaul23/java-project-99.git
cd java-project-99
./gradlew bootRun
```

By default, the application uses the `dev` profile and an in-memory H2 database.

After startup, the application is available at:

[http://localhost:8080](http://localhost:8080)

Use the demo account to sign in:

- Email: `hexlet@example.com`
- Password: `qwerty`

Alternatively, use the Makefile:

```bash
make setup
make start
```

`make setup` builds the application, and `make start` runs the assembled JAR.

## Rebuilding the frontend

The built frontend is stored in `src/main/resources/static/` and is shipped with the Spring Boot application. After changing the frontend version or the security transformations, rebuild and verify the bundle:

```bash
npm ci --ignore-scripts --no-audit --no-fund
node scripts/build-secure-frontend.mjs
node scripts/check-frontend.mjs
```

The build script applies the project's fixes to `@hexlet/java-task-manager-frontend`, creates a new bundle, and updates its reference in `index.html`. The verification script prevents JWTs, user data, or passwords from returning to persistent storage and checks task-filter and user-directory compatibility.

## Tests and quality checks

Every push is verified by two GitHub Actions workflows:

- `Build` reproducibly builds the published frontend, checks that JWTs, user data, and passwords cannot persist in browser storage, runs the Java tests and Spotless, and builds the executable JAR;
- `Hexlet Check` runs end-to-end browser scenarios covering authentication, users, task statuses, labels, tasks, and filtering.

Run the automated checks:

```bash
./gradlew clean check
```

Verify Java code formatting:

```bash
./gradlew spotlessCheck
```

Build the executable Spring Boot JAR:

```bash
./gradlew bootJar
```

## Production configuration

Configure the following environment variables for production:

| Variable | Purpose |
| --- | --- |
| `PORT` | Application HTTP port. Defaults to `8080`. |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile. Use `prod` in production. |
| `DB_HOST` | PostgreSQL host. |
| `DB_PORT` | PostgreSQL port. Defaults to `5432`. |
| `DB_NAME` | PostgreSQL database name. |
| `DB_USER` | PostgreSQL user. |
| `DB_PASSWORD` | PostgreSQL password. |
| `SENTRY_DSN` | Used by the application to send error events to Sentry. |
| `SENTRY_AUTH_TOKEN` | Used by Sentry build and source-context tooling. |
| `SENTRY_ORG` | Sentry organization used for source-context tooling. |
| `SENTRY_PROJECT` | Sentry project used for source-context tooling. |

For production, set:

```text
SPRING_PROFILES_ACTIVE=prod
```

Secret values are supplied through environment variables and are not stored in the repository.

## Error monitoring

Application errors in production are reported to Sentry. This provides information that can be used to diagnose failures.

Sentry connection settings are supplied through environment variables. Tokens, DSNs, and other secret values are not stored in the source code.

## Project structure

The main backend code is located in `src/main/java/hexlet/code/`:

```text
src/main/java/hexlet/code/
  component/
  config/
  controller/
  dto/
  exception/
  mapper/
  model/
  repository/
  security/
  service/
  specification/
```

Application resources and configuration are located in `src/main/resources/`:

```text
src/main/resources/
  static/
  application*.yml
```

Frontend build scripts are located in `scripts/`, and CI configuration is located in `.github/workflows/`.
