package eu.hyperspace.ftsapp.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Fts App",
                description = "API микросервиса, взаимодействующего с Minio",
                version = "1.0.0",
                contact = @Contact(
                        name = "Кульбака Никита",
                        email = "nikita.kulbaka@mail.ru"
                )
        )
)
public class OpenApiConfig {
}
