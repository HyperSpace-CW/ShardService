# Этап сборки с Gradle
FROM gradle:8.12.0-jdk21 as builder

# Устанавливаем рабочую директорию для сборки
WORKDIR /build

# Копируем исходные файлы и файлы сборки
COPY gradle/libs.versions.toml ./gradle/libs.versions.toml
COPY build.gradle .
COPY settings.gradle .
COPY src ./src
COPY .env .

# Собираем проект
RUN gradle --no-daemon clean build --stacktrace

# Финальный образ
FROM openjdk:21-ea-slim

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем собранный JAR из этапа builder
COPY --from=builder /build/build/libs/FtsApp-0.0.1-SNAPSHOT.jar /app/app.jar

# Открываем порт для приложения
EXPOSE 3015

# Команда для запуска приложения
ENTRYPOINT ["java", "-jar", "app.jar"]