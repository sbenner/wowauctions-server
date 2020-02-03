package com.heim.wowauctions.common.persistence.models;

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/4/14
 * Time: 11:42 PM
 */
@Data
@SolrDocument(collection = "auction_url")
public class AuctionUrl {
    @Id
    String id;
    @Field("last_modified_l")
    private Long lastModified;
    @Field("url_s")
    private String url;
    @Field("realm_s")
    private String realm;

}
