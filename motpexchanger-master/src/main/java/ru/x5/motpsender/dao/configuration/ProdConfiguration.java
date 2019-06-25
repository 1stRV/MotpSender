package ru.x5.motpsender.dao.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:motp-api-prod.properties")
@PropertySource("classpath:motp-api.properties")
@Profile("prod")
public class ProdConfiguration {
}
