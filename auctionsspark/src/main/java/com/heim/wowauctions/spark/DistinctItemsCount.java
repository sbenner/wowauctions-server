package com.heim.wowauctions.spark;

import com.heim.wowauctions.service.persistence.models.ArchivedAuction;
import com.mongodb.spark.MongoSpark;
import com.mongodb.spark.rdd.api.java.JavaMongoRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by sbenner on 30/05/2017.
 */
@Component
public class DistinctItemsCount {


    @Autowired
    private SparkSession sparkSession;

    @Autowired
    private JavaSparkContext javaSparkContext;

    @Scheduled(fixedRate = 60000)
    public void count() {


        Dataset<ArchivedAuction> explicitDS =
                MongoSpark.load(javaSparkContext).toDS(ArchivedAuction.class);

        explicitDS.printSchema();
        explicitDS.show();


            explicitDS.createOrReplaceTempView("archive");
//
            Dataset<Row> centenarians = sparkSession.
                    sql("SELECT itemId,buyout,quantity from archive where itemId='38925' group by itemId,buyout,quantity order by buyout");
            centenarians.show();

    }
}

