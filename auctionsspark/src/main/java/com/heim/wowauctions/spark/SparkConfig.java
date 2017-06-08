package com.heim.wowauctions.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by sbenner on 30/05/2017.
 */
@Configuration
public class SparkConfig {


    @Bean
    public SparkConf sparkConf() {
        SparkConf sc = new SparkConf()
                 .setMaster("local")
                //.setMaster("spark://localhost:7077")
                .setAppName("WowAuctionsAggregator")
                .set("spark.deploy.defaultCores","10")
                .set("spark.mongodb.input.uri", "mongodb://127.0.0.1/wowauctions.auctionsArchive")
                .set("spark.mongodb.output.uri", "mongodb://127.0.0.1/wowauctions.archivedCharts");
        return sc;
    }

    @Bean
    public JavaSparkContext javaSparkContext() {

        return new JavaSparkContext(sparkConf());
    }

    @Bean
    public SparkSession sparkSession() {
        return SparkSession
                .builder()
                .sparkContext(javaSparkContext().sc())
                .getOrCreate();
    }

}
