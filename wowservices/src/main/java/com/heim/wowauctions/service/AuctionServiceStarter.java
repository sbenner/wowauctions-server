package com.heim.wowauctions.service;

import com.heim.wowauctions.service.persistence.dao.MongoAuctionsDao;
import com.mongodb.Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

import java.util.Properties;


@SpringBootApplication
@ServletComponentScan
@ComponentScan("com.heim.wowauctions.service")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, PersistenceExceptionTranslationAutoConfiguration.class})
@EnableCaching
public class AuctionServiceStarter {

    @Autowired
    Mongo mongo;
    @Value("${spring.data.mongodb.database}")
    private String database;

    public static void main(String[] args) {
        Class cls = AuctionServiceStarter.class;
        SpringApplication app = new SpringApplication(new Object[]{cls});
        app.setDefaultProperties(getDefaultProperties(cls));

        app.run(args);

    }

    @Bean
    String database() {
        return database;
    }

    @Bean
    MongoAuctionsDao mongoTemplate() {
        return new MongoAuctionsDao(mongo, database);
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }


    private static Properties getDefaultProperties(Class cls) {
        Properties properties = new Properties();
        if (cls.getPackage().getImplementationVersion() == null) {
            properties.setProperty("version", "unknown");
        } else {
            properties.setProperty("version", cls.getPackage().getImplementationVersion());
        }

        return properties;
    }

}
