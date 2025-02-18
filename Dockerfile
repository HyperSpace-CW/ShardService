# Используем официальный образ Java
FROM openjdk:23-ea-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем файл сборки (например, Maven)
COPY build/libs/FtsApp-0.0.1-SNAPSHOT.jar /app/app.jar

# Открываем порт для приложения
EXPOSE 8080

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]