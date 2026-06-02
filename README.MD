# Руководство к запуску и тестированию проекта

## В терминале:
### 1. Перейти из корневой папки в папку с sql скриптом
- cd DealMS\src\main\java\app\gozenko\db\
выполнить
- psql -h localhost -p 5432 -U <имя_пользователя> -d postgres -f init.sql

Таблицы автоматически создадутся при первом запуске благодаря Liquibase миграции
(файл - src/main/resources/db/changeLog/db.changelog-master.xml)
Таблицы сопоставятся с существующими моделями java благодаря "spring.jpa.hibernate.ddl-auto=validate"

### 2. Установить переменные окружения для подключения к БД
- set DB_USERNAME=имя_пользователя
- set DB_PASSWORD=твой_пароль

### 3. Swagger 
- http://localhost:8086/swagger-ui/index.html - DealMS
- http://localhost:8085/swagger-ui/index.html - CalculatorMS
- http://localhost:8087/swagger-ui/index.html - StatementMS
- http://localhost:8088/swagger-ui/index.html - DossierMS
- http://localhost:8089/swagger-ui/index.html - GatewayMS

## Для теста DossierMS:
### 4. Установить переменную окружения для отправки письма на почту
- Создание токена авторизации:
  - Включить двухфакторную аутентификацию в своей гугл почте
  - Перейти в управление аккаунтом Google -> Безопасность
  - В разделе "Вход в Google" выбрать "Пароли приложений"
  - Вбить в строке название приложения и сгенерировать пароль (токен)
- Изменить почту на свою:
  - spring.mail.username=gozenkodara80@gmail.com - в DossierMS
  - mail.receiver=gozenkodara80@gmail.com - в DealMS
- В терминале: set MAIL_PASSWORD = созданный_токен

### 5. Для запуска docker контейнеров (Kafka и Zookeeper)
- В терминале в корне проекта выполнить: docker-compose up -d

### 6. Для запуска интеграционного теста
- проверить что движок docker запущен


# Запуск и тестирование через контейнеры
## 1. Установить переменные окружения
Создать файл .env в корне проекта:
  - DB_USERNAME=<значение>
  - DB_PASSWORD=<значение>
  - MAIL_USERNAME=<значение>
  - MAIL_PASSWORD=<значение>
  - COVERALLS_REPO_TOKEN=<значение>
## 2. Через терминал выполнить
- docker-compose up -d --build

## 3. Дополнительно
Команда очистки кеша сборки:
- docker builder prune -a -f

Команда для генерации coveralls отчета:
- mvn clean test jacoco:report coveralls:report -DrepoToken (из .env)

## 4. Адреса контейнеров микросервисов внутри docker-сети:
- http://calculator-ms:8084 - CalculatorMS
- http://deal-ms:8084 - DealMS
- http://statement-ms:8084 - StatementMS
- http://dossier-ms:8084 - DossierMS
- http://gateway-ms:8084 - GatewayMS