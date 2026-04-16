# Создание базы данных

## В терминале:
### 1. Перейти из корневой папки в папку с sql скриптом
- cd DealMS\src\main\java\app\gozenko\db\
выполнить
- psql -h localhost -p 5432 -U <имя_пользователя> -d deal_db -f init.sql

Таблицы обновятся автоматически благодаря "spring.jpa.hibernate.ddl-auto=update"

### 2. Установить переменные окружения для подключения к БД
- set DB_USERNAME=имя_пользователя
- set DB_PASSWORD=твой_пароль

### 3. Swagger 
- http://localhost:8086/swagger-ui/index.html - DealMS
- http://localhost:8085/swagger-ui/index.html - CalculatorMS
- http://localhost:8087/swagger-ui/index.html - StatementMS