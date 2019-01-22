package com.heim.wowauctions.common.persistence.models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;


/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/4/14
 * Time: 11:47 PM
 */

@Data
@SolrDocument(collection = "feedback")
public class Feedback {

    private long timestamp;
    private String feedback;
    private String from;
    @Id
    String id;
}
