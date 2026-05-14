package rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import persistence.hibernate.ProbaHibernateRepo;
import persistence.interfaces.IProbaRepository;

/**
 * Configuratie Spring Boot:
 * Inregistreaza ProbaHibernateRepo ca bean, astfel incat
 * ProbaRestController il poate primi prin injectare.
 */
@Configuration
public class AppConfig {
    @Bean
    public IProbaRepository probaRepository() {
        return new ProbaHibernateRepo();
    }
}
