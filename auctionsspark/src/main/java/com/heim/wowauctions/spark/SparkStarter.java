package com.heim.wowauctions.spark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by sbenner on 06/06/2017.
 */
@EnableScheduling
@SpringBootApplication
public class SparkStarter {


    public static void main(String[] args) {
        Class cls = SparkStarter.class;
        SpringApplication app = new SpringApplication(new Object[]{cls});

        app.run();


    }


}
