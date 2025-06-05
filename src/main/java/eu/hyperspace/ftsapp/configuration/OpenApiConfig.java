package eu.hyperspace.ftsapp.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@OpenAPIDefinition(
        info = @Info(
                title = "Shard app",
                description = "API для взаимодействия с шардами и файлами",
                version = "1.0.0"
        )
)
public class OpenApiConfig {
}
