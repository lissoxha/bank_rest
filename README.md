# Bank Cards Management System

Система управления банковскими картами на Spring Boot с поддержкой JWT аутентификации, ролевого доступа и REST API.

##  Технологии

- **Java 17+**
- **Spring Boot 3.2.0**
- **Spring Security** с JWT
- **Spring Data JPA**
- **PostgreSQL**
- **Liquibase** для миграций
- **Docker & Docker Compose**
- **Swagger/OpenAPI** для документации

##  Функциональность

### Аутентификация и авторизация
- JWT токены для аутентификации
- Роли: `ADMIN` и `USER`
- Регистрация и вход в систему

### Возможности администратора
- Создание, блокировка, активация и удаление карт
- Управление пользователями
- Просмотр всех карт и транзакций

### Возможности пользователя
- Просмотр своих карт с поиском и пагинацией
- Запрос блокировки карты
- Переводы между своими картами
- Просмотр баланса и истории транзакций

##  Установка и запуск

### Предварительные требования
- Java 17+
- Maven 3.6+
- Docker и Docker Compose
- PostgreSQL (если запускаете локально)

### Запуск через Docker Compose

1. **Клонируйте репозиторий:**
```bash
git clone <repository-url>
cd bank_rest
```

2. **Запустите только базу данных:**
```bash
docker-compose up postgres -d
```

3. **Соберите и запустите приложение:**
```bash
mvn clean package
docker-compose --profile app up app
```

### Локальный запуск

1. **Запустите PostgreSQL:**
```bash
docker-compose up postgres -d
```

2. **Соберите проект:**
```bash
mvn clean package
```

3. **Запустите приложение:**
```bash
java -jar target/bank-cards-1.0.0.jar
```

##  API Документация

После запуска приложения документация доступна по адресу:
- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8080/v3/api-docs

##  Тестовые пользователи

Система создает тестовых пользователей при первом запуске:

### Администратор
- **Username:** `admin`
- **Password:** `password123`
- **Email:** `admin@bank.com`

### Обычный пользователь
- **Username:** `user1`
- **Password:** `password123`
- **Email:** `user1@bank.com`

##  Примеры использования API

### 1. Аутентификация

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "user1",
    "password": "password123"
  }'
```

### 2. Получение карт пользователя

```bash
curl -X GET http://localhost:8080/api/cards \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### 3. Перевод между картами

```bash
curl -X POST http://localhost:8080/api/transactions/transfer \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "amount": 100.00,
    "fromCardId": 1,
    "toCardId": 2,
    "description": "Transfer to savings"
  }'
```

##  База данных

### Структура таблиц
- **users** - пользователи системы
- **cards** - банковские карты
- **transactions** - транзакции

### Миграции
Миграции Liquibase находятся в `src/main/resources/db/migration/` и выполняются автоматически при запуске приложения.

##  Безопасность

- **Шифрование паролей:** BCrypt
- **Шифрование номеров карт:** Простое шифрование (для демо)
- **JWT токены:** HMAC SHA256
- **Маскирование данных:** Номера карт отображаются как `**** **** **** 1234`
- **Ролевой доступ:** Разделение прав между ADMIN и USER

##  Тестирование

### Запуск тестов
```bash
mvn test
```

### Покрытие тестами
```bash
mvn jacoco:report
```

##  Структура проекта

```
src/
├── main/
│   ├── java/com/example/bankcards/
│   │   ├── config/          # Конфигурация Spring
│   │   ├── controller/     # REST контроллеры
│   │   ├── dto/           # Data Transfer Objects
│   │   ├── entity/        # JPA сущности
│   │   ├── exception/     # Обработка исключений
│   │   ├── repository/    # JPA репозитории
│   │   ├── security/      # Spring Security
│   │   ├── service/       # Бизнес-логика
│   │   └── util/          # Утилиты
│   └── resources/
│       ├── application.yml # Конфигурация приложения
│       └── db/migration/  # Liquibase миграции
└── test/                  # Тесты
```

##  Docker

### Сборка образа
```bash
docker build -t bank-cards-app .
```

### Запуск контейнера
```bash
docker run -p 8080:8080 bank-cards-app
```

##  Мониторинг

Приложение логирует:
- Аутентификацию пользователей
- Операции с картами
- Транзакции
- Ошибки и исключения

Логи сохраняются в файл `logs/bank-cards.log`.


##  Поддержка

При возникновении проблем:
1. Проверьте логи приложения
2. Убедитесь, что PostgreSQL запущен
3. Проверьте конфигурацию в `application.yml`
4. Создайте Issue в репозитории
