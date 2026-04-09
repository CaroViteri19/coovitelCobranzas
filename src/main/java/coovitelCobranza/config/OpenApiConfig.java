package coovitelCobranza.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de OpenAPI (Swagger) para la documentación de la API de Coovitel Cobranzas.
 * Define la información de la API, contacto y esquema de seguridad JWT para autenticación.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Crea la configuración de OpenAPI para la API de Coovitel Cobranzas.
     *
     * @return objeto OpenAPI configurado con información de la API, seguridad JWT y detalles de contacto
     */
    @Bean
    public OpenAPI coovitelOpenApi() {

        return new OpenAPI()
                .info(new Info()
                        .title("Coovitel Cobranzas API")
                        .description("API for debt collection management")
                        .version("v1")
                        .contact(new Contact()
                                .name("Coovitel Cobranzas Team")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}



