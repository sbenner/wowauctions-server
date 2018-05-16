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
        SparkConf sc = new SparkConf()
                .setMaster("local[7]")
                .set("spark.ui.enabled", "false")
                .setAppName("WowAuctionsAggregator")
                // .set("spark.deploy.defaultCores", "10")
//                .set("spark.driver.memory","5g")
//                .set("spark.executor.memory","4g")
               // .set("spark.executor.cores","7")
                .set("spark.memory.offHeap.size","2147483648")
                .set("spark.memory.offHeap.enabled","true")
                .set("spark.mongodb.input.partitionerOptions.partitionSizeMB","512")
                .set("spark.mongodb.input.partitionerOptions.partitionKey","itemId")
                .set("spark.mongodb.input.partitioner","MongoSplitVectorPartitioner")
                .set("spark.mongodb.input.uri", "mongodb://127.0.0.1/wowauctions.auctionsArchive")
                .set("spark.mongodb.output.uri", "mongodb://127.0.0.1/wowauctions.archivedCharts");
        return sc;
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
