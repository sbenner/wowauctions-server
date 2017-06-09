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
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.List;

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

    public DeferredResult<Long> count() {


        Dataset<ArchivedAuction> explicitDS =
                MongoSpark.load(javaSparkContext).toDS(ArchivedAuction.class);

        explicitDS.printSchema();
        explicitDS.show();


        explicitDS.createOrReplaceTempView("archive");
//


        Dataset<Row> centenarians = sparkSession.
                sql("SELECT itemId,buyout,quantity,timestamp from archive where itemId='38925' group by itemId,buyout,quantity,timestamp order by buyout");


        StructType schema =
                new StructType().add("itemId", LongType).add("buyout", LongType).add("quantity", IntegerType).add("timestamp", LongType);


        Dataset<Row> modified = centenarians.map(
                (MapFunction<Row, Row>) r ->
                {
                    int quantity = r.getInt(2);
                    if (quantity > 0) {
                        System.out.println("QUANTITY IS GREATER THAN 0");
                        long b = r.getLong(1) / quantity;
                        long id = r.getAs("itemId");
                        return RowFactory.create(id, b, 1, r.getLong(3));
                    } else {
                        return r;
                    }
                }, RowEncoder.apply(schema)
        );


        long count = modified.count();
        System.out.println("COUNT : "+count);
        System.out.println("#################################################################");

        return new DeferredResult<>(count);



    }
}

