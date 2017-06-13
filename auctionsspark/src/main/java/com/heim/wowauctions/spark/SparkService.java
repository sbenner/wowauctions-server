package com.heim.wowauctions.spark;


import com.heim.wowauctions.common.persistence.models.ArchivedAuction;
import com.heim.wowauctions.common.persistence.models.Item;
import com.heim.wowauctions.common.persistence.models.ItemChartData;
import com.heim.wowauctions.common.persistence.repositories.ItemChartDataRepository;
import com.mongodb.Mongo;
import com.mongodb.spark.MongoSpark;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.StructType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.spark.sql.types.DataTypes.IntegerType;
import static org.apache.spark.sql.types.DataTypes.LongType;


/**
 * Created by sbenner on 30/05/2017.
 */
@Component
public class SparkService {


    private final SparkSession sparkSession;

    private final JavaSparkContext javaSparkContext;

    final
    ItemChartDataRepository itemChartDataRepository;

    @Autowired
    public SparkService(SparkSession sparkSession, JavaSparkContext javaSparkContext, ItemChartDataRepository itemChartDataRepository) {
        this.sparkSession = sparkSession;
        this.javaSparkContext = javaSparkContext;
        this.itemChartDataRepository = itemChartDataRepository;
    }

    public Long count() {


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




        List<Row>  modified =

                centenarians.map(  (MapFunction<Row, Row>) r ->
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
        ).collectAsList();//.write().option("collection", "archivedCharts").mode("overwrite").save();


        Map<Long,ItemChartData> map = new HashMap<>();
        ItemChartData itemChartData = null;
        for(Row r : modified){
            Long id = r.getAs(0);
            if(id!=null && map.containsKey(id)){
                itemChartData = map.get(id);
                long value = r.getAs(1);
                long timestamp = r.getAs(3);
                itemChartData.getValueTime().put(value,timestamp);
                map.put(id,itemChartData);
            }else{
                itemChartData = new ItemChartData();
                itemChartData.setItemId(id);
                Map<Long,Long> values = new HashMap<>();
                long value = r.getAs(1);
                long timestamp = r.getAs(3);
                values.put(value,timestamp);
                itemChartData.setValueTime(values);
                map.put(id,itemChartData);
            }

        }

    for(Map.Entry<Long,ItemChartData> e: map.entrySet()){
            ItemChartData i = e.getValue();
            itemChartDataRepository.save(i);
    }


//
//
//
//
//        System.out.println("COUNT : " + modified.size());
       //long count = modified.size();
       // System.out.println("COUNT : " + count);
        System.out.println("#################################################################");
        return 0L;
    }
}

