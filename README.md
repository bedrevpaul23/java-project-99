# Task Manager

[![Actions Status](https://github.com/bedrevpaul23/java-project-99/actions/workflows/hexlet-check.yml/badge.svg)](https://github.com/bedrevpaul23/java-project-99/actions)
[![Build](https://github.com/bedrevpaul23/java-project-99/actions/workflows/build.yml/badge.svg)](https://github.com/bedrevpaul23/java-project-99/actions/workflows/build.yml)

[Русский](#Русский) · [English](#English)

# Русский

## О проекте

Task Manager — веб-приложение для управления задачами, пользователями, статусами задач и метками. В приложении есть браузерный интерфейс и REST API. Защищённые операции требуют JWT-аутентификации.

## Демо

Приложение доступно по адресу [java-project-99-frxd.onrender.com](https://java-project-99-frxd.onrender.com).

Приложение размещено на Render. После периода неактивности первый запуск может занять некоторое время.

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

- `Title cont` — поиск по части названия задачи;
- `Assignee` — фильтрация по назначенному исполнителю;
- `Status` — фильтрация по статусу;
- `Label` — фильтрация по метке.

Несколько фильтров можно применять одновременно.

### Пользователи

В разделе пользователей можно просматривать список пользователей и создавать новые учётные записи. Пользователь может редактировать и удалять только собственную учётную запись. Удаление невозможно, если пользователь назначен исполнителем существующей задачи.

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
- Spring Boot 3.4
- Spring MVC
- Spring Data JPA / Hibernate
- Spring Security
- JWT / OAuth2 Resource Server
- PostgreSQL
- H2
- Gradle
- Springdoc OpenAPI / Swagger UI
- Sentry
- Docker
- GitHub Actions
- Render
- Hexlet Task Manager frontend

## Локальный запуск

Для локального запуска требуется JDK 21.

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

## Тесты и проверки

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
| `SPRING_PROFILES_ACTIVE` | Активный профиль Spring. Для рабочего окружения используется значение `prod`. |
| `DB_HOST` | Адрес сервера PostgreSQL. |
| `DB_PORT` | Порт PostgreSQL. |
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
  config/
  controller/
  dto/
  exception/
  mapper/
  model/
  repository/
  service/
  specification/
```

Ресурсы и конфигурация приложения расположены в `src/main/resources/`:

```text
src/main/resources/
  static/
  application*.yml
```

---

# English

## About

Task Manager is a web application for managing tasks, users, task statuses, and labels. It includes a browser UI and a REST API. Protected operations require JWT authentication.

## Live demo

The application is available at [java-project-99-frxd.onrender.com](https://java-project-99-frxd.onrender.com).

The application is hosted on Render. After a period of inactivity, the first start may take some time.

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

- `Title cont` — searches for part of a task title;
- `Assignee` — filters by the assigned user;
- `Status` — filters by task status;
- `Label` — filters by label.

Multiple filters can be applied at the same time.

### Users

The Users section lets you view users and create new accounts. A user can edit and delete only their own account. An account cannot be deleted while that user is assigned to an existing task.

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
- Spring Boot 3.4
- Spring MVC
- Spring Data JPA / Hibernate
- Spring Security
- JWT / OAuth2 Resource Server
- PostgreSQL
- H2
- Gradle
- Springdoc OpenAPI / Swagger UI
- Sentry
- Docker
- GitHub Actions
- Render
- Hexlet Task Manager frontend

## Local development

JDK 21 is required.

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

## Tests and quality checks

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
| `SPRING_PROFILES_ACTIVE` | Active Spring profile. Use `prod` in production. |
| `DB_HOST` | PostgreSQL host. |
| `DB_PORT` | PostgreSQL port. |
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
  config/
  controller/
  dto/
  exception/
  mapper/
  model/
  repository/
  service/
  specification/
```

Application resources and configuration are located in `src/main/resources/`:

```text
src/main/resources/
  static/
  application*.yml
```