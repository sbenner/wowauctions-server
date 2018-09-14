package com.heim.wowauctions.spark;

import com.heim.wowauctions.common.persistence.dao.MongoAuctionsDao;
import com.mongodb.Mongo;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by sbenner on 30/05/2017.
 */
@Configuration
public class SparkConfig {

    @Value("${spring.data.mongodb.database}")
    String dbName;

    @Autowired
    private Mongo mongo;


    @Bean
    public SparkConf sparkConf() {
        return new SparkConf()
                .setMaster("local[4]")
                .set("spark.ui.enabled", "false")
                .setAppName("WowAuctionsAggregator")
                // .set("spark.deploy.defaultCores", "10")
                .set("spark.driver.memory", "8g")
                .set("spark.executor.memory", "8g")
               // .set("spark.executor.cores","7")
                .set("spark.mongodb.input.partitioner","MongoSplitVectorPartitioner")
                .set("spark.mongodb.input.uri", "mongodb://127.0.0.1/wowauctions.auctionsArchive")
                .set("spark.mongodb.output.uri", "mongodb://127.0.0.1/wowauctions.archivedCharts");
    }

    @Bean
    public JavaSparkContext javaSparkContext() {
        return new JavaSparkContext(sparkConf());
    }

    @Bean
    public MongoAuctionsDao mongoTemplate() {
        return new MongoAuctionsDao(mongo, dbName);
    }

    @Bean
    public SparkSession sparkSession() {
        return SparkSession
                .builder()
                .sparkContext(javaSparkContext().sc())
                .getOrCreate();
    }

}
