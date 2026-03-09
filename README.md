# Трекер привычек (Habit Tracker)

Приложение для отслеживания привычек с функционалом для пользователей и администраторов.

## Возможности

### Для пользователей
- Регистрация и вход
- Создание, редактирование и удаление привычек
- Отметка выполнения привычек (ежедневно/еженедельно)
- Просмотр статистики и серий (streak)
- Редактирование профиля
- Сброс пароля

### Для администраторов
- Вход в админ-панель
- Просмотр списка всех пользователей
- Блокировка/разблокировка пользователей
- Просмотр аудит-логов действий пользователей

## Требования

- Java Development Kit (JDK) 17+
- Docker и Docker Compose
- Node.js (для разработки фронтенда)

## Сборка и запуск

### Быстрый старт (Docker Compose)

```bash
# Клонирование репозитория
git clone https://github.com/ne-kit-28/y_lab_homework.git
cd y_lab_homework

# Сборка и запуск всех сервисов
docker-compose up --build
```

После запуска будут доступны:
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **pgAdmin**: http://localhost:5050 (email: nickit.nic098@yandex.ru, password: postgres)

### Запуск вручную (без Docker)

```bash
# 1. Запуск PostgreSQL
docker run -d -p 5433:5432 -e POSTGRES_USER=y_lab_user -e POSTGRES_PASSWORD=Y_lab_password -e POSTGRES_DB=y_lab postgres:15

# 2. Сборка стартера
cd audit-logging-spring-boot-starter
mvn clean install -DskipTests
cd ..

# 3. Сборка бэкенда
cd homework2
mvn clean package -DskipTests
cd ..

# 4. Запуск бэкенда
cd homework2
java -jar target/habits-latest.jar

# 5. Запуск фронтенда (в отдельном терминале)
cd frontend
npm install
npm run dev
```

## Использование

### Пользовательское приложение

1. Откройте http://localhost:3000
2. Зарегистрируйте новый аккаунт или войдите
3. Создавайте привычки с указанием частоты (ежедневно/еженедельно)
4. Отмечайте выполнение кнопкой "✓ Done Today"
5. Смотрите статистику по кнопке "📊 Stats"

### Админ-панель

1. Откройте http://localhost:3000/admin/login
2. Войдите с учётными данными администратора
3. Просматривайте список пользователей
4. Блокируйте/разблокируйте пользователей
5. Смотрите аудит-логи действий пользователей

## API Endpoints

### Аутентификация
- `POST /login/signIn` - Вход
- `POST /login/signUp` - Регистрация
- `POST /login/requestReset/{email}` - Запрос сброса пароля
- `POST /login/reset` - Сброс пароля

### Привычки
- `GET /habit/{userId}/all/{filter}` - Список привычек
- `GET /habit/{userId}/{name}` - Получить привычку
- `POST /habit/{userId}/create` - Создать привычку
- `PUT /habit/{userId}/{habitId}` - Обновить привычку
- `DELETE /habit/{userId}/{habitId}` - Удалить привычку

### Прогресс
- `POST /progress/{userId}/create/{habitId}` - Отметить выполнение
- `GET /progress/{userId}/streak/{habitId}` - Получить серию
- `GET /progress/{userId}/statistic/{habitId}/{period}` - Статистика
- `GET /progress/{userId}/report/{habitId}/{period}` - Отчёт

### Пользователь
- `GET /user/{userId}/{email}` - Получить пользователя
- `PUT /user/{userId}` - Обновить профиль
- `DELETE /user/{userId}` - Удалить аккаунт

### Админ
- `GET /admin/user/all` - Все пользователи
- `GET /admin/user/{userId}/block/{status}` - Блокировка
- `GET /admin/user/{userId}/audit` - Аудит пользователя

## Структура проекта

```
y_lab_homework/
├── frontend/                 # Next.js фронтенд
│   ├── pages/               # Страницы приложения
│   │   ├── index.js        # Вход
│   │   ├── signup.js       # Регистрация
│   │   ├── dashboard.js    # Управление привычками
│   │   ├── profile.js      # Профиль пользователя
│   │   ├── reset.js        # Сброс пароля
│   │   └── admin/          # Админ-панель
│   └── components/         # React компоненты
├── homework2/               # Java Spring Boot бэкенд
│   └── src/main/java/
│       └── y_lab/
│           ├── controller/  # REST контроллеры
│           ├── service/    # Бизнес-логика
│           ├── repository/ # Работа с БД
│           └── config/     # Конфигурация
├── audit-logging-spring-boot-starter/  # Кастомный starter
└── docker-compose.yaml     # Docker Compose конфиг
```

## Технологии

- **Frontend**: Next.js 14, React 18
- **Backend**: Java 17, Spring Boot
- **Database**: PostgreSQL 15
- **Authentication**: JWT
- **Logging**: Custom audit-logging-starter
