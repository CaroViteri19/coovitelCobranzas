package coovitelCobranza;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot para el sistema de cobranzas de Coovitel.
 * Inicia la aplicación y configura automáticamente los beans y componentes de Spring.
 */
@SpringBootApplication
public class Application {

	/**c
	 * Punto de entrada de la aplicación.
	 *
	 * @param args argumentos de línea de comandos pasados a la aplicación
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
