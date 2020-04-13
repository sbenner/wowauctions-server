package com.heim.wowauctions.spark;


import com.heim.wowauctions.common.persistence.dao.MongoAuctionsDao;
import com.heim.wowauctions.common.persistence.dao.MongoService;
import com.heim.wowauctions.common.persistence.models.ArchivedAuction;
import com.heim.wowauctions.common.persistence.models.ItemChartData;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.spark.sql.types.DataTypes.IntegerType;
import static org.apache.spark.sql.types.DataTypes.LongType;


@Component
public class SparkService {


    private final SparkSession sparkSession;

    @Autowired
    private final MongoAuctionsDao mongoAuctionsDao;

    private final MongoService mongoService;


    private final JavaSparkContext javaSparkContext;

    @Autowired
    public SparkService(SparkSession sparkSession,
                        JavaSparkContext javaSparkContext,
                        MongoAuctionsDao mongoAuctionsDao,
                        MongoService mongoService
    ) {
        this.sparkSession = sparkSession;
        this.javaSparkContext = javaSparkContext;

        this.mongoService = mongoService;
        this.mongoAuctionsDao = mongoAuctionsDao;
    }

    @Scheduled(fixedRate = 3600000)
    public void count() {


        Dataset<ArchivedAuction> explicitDS =
                MongoSpark.load(javaSparkContext).toDS(ArchivedAuction.class);
        explicitDS.printSchema();
        explicitDS.show();
        explicitDS.createOrReplaceTempView("archive");

        Dataset<Row> centenarians = sparkSession.
                sql("SELECT itemId,case when buyout=0 " +
                        "  then ppi " +
                        " else buyout " +
                        "  end " +
                        " end as buyout," +
                        " quantity," +
                        " timestamp " +
                        "from archive group by itemId," +
                        "buyout,quantity," +
                        "timestamp order by buyout");


        StructType schema =
                new StructType()
                        .add("itemId", LongType)
                        .add("buyout", LongType)
                        .add("quantity", IntegerType)
                        .add("timestamp", LongType);

        List<Row> modified =
                centenarians.map((MapFunction<Row, Row>) r ->
                        {
                            int quantity = r.getInt(2);
                            if (quantity > 0) {
                                long b = r.getLong(1) / quantity;
                                long id = r.getAs("itemId");
                                return RowFactory.create(id, b, 1, r.getLong(3));
                            } else {
                                return r;
                            }
                        }, RowEncoder.apply(schema)
                ).collectAsList();

        Map<Long, ItemChartData> map = new HashMap<>();
        ItemChartData itemChartData = null;

        for (Row r : modified) {
            Long id = r.getAs(0);
            if (id != null) {
                if (map.containsKey(id)) {
                    itemChartData = map.get(id);
                    long value = r.getAs(1);
                    long timestamp = r.getAs(3);
                    itemChartData.getValueTime().put(value, timestamp);
                } else {
                    itemChartData = new ItemChartData();
                    itemChartData.setItemId(id);
                    Map<Long, Long> values = new HashMap<>();
                    long value = r.getAs(1);
                    long timestamp = r.getAs(3);
                    values.put(value, timestamp);
                    itemChartData.setValueTime(values);
                }
                map.put(id, itemChartData);
            }

        }
        //long timestamp = mongoAuctionsDao.getAuctionUrl().values().forEach(i -> i.setTimestamp(timestamp));
        if (itemChartData != null)
            mongoService.saveItemChart(itemChartData);

        System.out.println("#################################################################");

        sparkSession.stop();


    }
}

