package com.heim.wowauctions.spark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by sbenner on 06/06/2017.
 */
@SpringBootApplication
public class SparkStarter {


    public static void main(String[] args) {
        Class cls = SparkStarter.class;
        SpringApplication app = new SpringApplication(new Object[]{cls});

        app.run();


    }


}
