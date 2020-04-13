package com.heim.wowauctions.spark;

import com.heim.wowauctions.common.persistence.dao.MongoAuctionsDao;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.SimpleMongoClientDbFactory;

/**
 * Created by sbenner on 30/05/2017.
 */
@Configuration

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class SparkConfig {


    @Bean
    public SparkConf sparkConf() {
        return new SparkConf()
                .setMaster("local[2]")
                .set("spark.ui.enabled", "false")
                .setAppName("WowAuctionsAggregator")

                .set("spark.driver.memory","2g")
                .set("spark.executor.memory","1g")
               .set("spark.mongodb.input.partitioner","MongoSplitVectorPartitioner")
                // .set("spark.executor.cores","7")
                .set("spark.mongodb.input.uri", "mongodb://127.0.0.1/wowauctions.auctionsArchive")
                .set("spark.mongodb.output.uri", "mongodb://127.0.0.1/wowauctions.archivedCharts");
    }

    @Bean
    public JavaSparkContext javaSparkContext() {
        return new JavaSparkContext(sparkConf());
    }


    public @Bean
    MongoAuctionsDao mongoTemplate() {
        return new MongoAuctionsDao(mongoDbFactory());
    }


    public @Bean
    MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }


    @Bean
    public MongoDbFactory mongoDbFactory() {
        return new SimpleMongoClientDbFactory(
                mongoClient(), "wowauctions");
    }


    @Bean
    public SparkSession sparkSession() {
        return SparkSession
                .builder()
                .sparkContext(javaSparkContext().sc())
                .getOrCreate();
    }

}
