package ee.kymmepunkti;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(
		title = "Decathlon Points API",
		version = "1.0",
		description = "Calculates points for men's decathlon events"
))
@SpringBootApplication
public class KymmePunktiApplication {

	static void main(String[] args) {
		SpringApplication.run(KymmePunktiApplication.class, args);
	}

}
