package com.heim.wowauctions.spark;


import com.heim.wowauctions.common.persistence.models.ArchivedAuction;
import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static org.apache.spark.sql.types.DataTypes.IntegerType;
import static org.apache.spark.sql.types.DataTypes.LongType;


/**
 * Created by sbenner on 30/05/2017.
 */
@Component
public class SparkService {


    @Autowired
    private SparkSession sparkSession;

    @Autowired
    private JavaSparkContext javaSparkContext;

  // @Scheduled(fixedRate = 60000)
    public void count() {


        Dataset<ArchivedAuction> explicitDS =
                MongoSpark.load(javaSparkContext).toDS(ArchivedAuction.class);

        explicitDS.printSchema();
        explicitDS.show();


        explicitDS.createOrReplaceTempView("archive");
//


        Dataset<Row> centenarians = sparkSession.
                sql("SELECT itemId,buyout,quantity from archive where itemId='38925' group by itemId,buyout,quantity order by buyout");


        StructType schema = new StructType().add("itemId", LongType).add("buyout", LongType).add("quantity", IntegerType);


       Dataset<Row> modified=   centenarians.map(
                (MapFunction<Row, Row>) r ->
                {
                    int q = r.getInt(2);
                    if (q > 0) {
                        System.out.println("QUANTITY IS GREATER THAN 0");
                        long b = r.getLong(1) / q;
                        long id = r.getAs("itemId");
                        return RowFactory.create(id, b, 1);
                    }else {
                        return r;
                    }
                }, RowEncoder.apply(schema)
        );
        modified.show();

        System.out.println("#################################################################");

    }
}

