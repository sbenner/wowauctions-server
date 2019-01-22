package com.heim.wowauctions.common.persistence.models;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Dynamic;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.Map;

/**
 * Created by sbenner on 13/06/2017.
 */

@Data
@SolrDocument(collection = "item_chart_data")
public class ItemChartData {
    @Id
    String id;
    @Indexed("item_id_l")
    private long itemId;

    @Dynamic
    @Field("*_l")
    private Map<Long,Long> valueTime;
    @Field("qty_")
    private int quantity;
    @Field("ts_l")
    private long timestamp;


}
