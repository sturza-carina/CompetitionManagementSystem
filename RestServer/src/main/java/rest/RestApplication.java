package rest;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * Clasa principala Spring Boot pentru serverul REST.
 * Ruleaza pe portul 8080 (implicit).
 */
@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class})
public class RestApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestApplication.class, args);
    }
}
