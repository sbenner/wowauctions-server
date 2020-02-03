package com.heim.wowauctions.spark;

<<<<<<< HEAD
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import com.heim.wowauctions.common.persistence.dao.MongoAuctionsDao;
=======
>>>>>>> df32b863781a4c0e3a4bffb240556b8a4fb1e98a
import com.mongodb.Mongo;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
<<<<<<< HEAD
=======

>>>>>>> df32b863781a4c0e3a4bffb240556b8a4fb1e98a
/**
 * Created by sbenner on 30/05/2017.
 */
@Configuration
<<<<<<< HEAD
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
=======
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
>>>>>>> df32b863781a4c0e3a4bffb240556b8a4fb1e98a
public class SparkConfig {

    //    @Value("${spring.data.mongodb.database}")
    String dbName;

    @Autowired
    private Mongo mongo;


    @Bean
    public SparkConf sparkConf() {
        return new SparkConf()
                .setMaster("local[2]")
                .set("spark.ui.enabled", "false")
                .setAppName("WowAuctionsAggregator")
<<<<<<< HEAD
                .set("spark.driver.memory","2g")
                .set("spark.executor.memory","1g")
               .set("spark.mongodb.input.partitioner","MongoSplitVectorPartitioner")
=======
                // .set("spark.deploy.defaultCores", "10")
                .set("spark.driver.memory", "8g")
                .set("spark.executor.memory", "8g")
               // .set("spark.executor.cores","7")
                .set("spark.mongodb.input.partitioner","MongoSplitVectorPartitioner")
>>>>>>> df32b863781a4c0e3a4bffb240556b8a4fb1e98a
                .set("spark.mongodb.input.uri", "mongodb://127.0.0.1/wowauctions.auctionsArchive")
                .set("spark.mongodb.output.uri", "mongodb://127.0.0.1/wowauctions.archivedCharts");
    }

    @Bean
    public JavaSparkContext javaSparkContext() {
        return new JavaSparkContext(sparkConf());
    }

//    @Bean
//    public MongoAuctionsDao mongoTemplate() {
//        return new MongoAuctionsDao(mongo, dbName);
//    }

    @Bean
    public SparkSession sparkSession() {
        return SparkSession
                .builder()
                .sparkContext(javaSparkContext().sc())
                .getOrCreate();
    }

}
