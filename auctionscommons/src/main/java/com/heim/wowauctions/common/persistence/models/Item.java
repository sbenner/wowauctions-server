package com.heim.wowauctions.common.persistence.models;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/5/14
 * Time: 1:01 AM
 */
@Data
@SolrDocument(collection = "item")
public class Item {

    @Id
    private String id;
    @Field("item_id_l")
    private long itemId;
    @Field("name_s")
    private String name;
    @Field("item_level_i")
    private int itemLevel;
    @Field("quality_i")
    private int quality;



}


