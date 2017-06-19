package com.heim.wowauctions.spark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Created by sbenner on 06/06/2017.
 */
@SpringBootApplication
@EnableMongoRepositories(basePackages = {"com.heim.wowauctions.common.persistence"})
@ComponentScan(basePackages = {"com.heim.wowauctions.common.persistence","com.heim.wowauctions.spark"})
public class SparkStarter {


    public static void main(String[] args) {
        Class cls = SparkStarter.class;
        SpringApplication app = new SpringApplication(new Object[]{cls});
        app.run();


    }


}
