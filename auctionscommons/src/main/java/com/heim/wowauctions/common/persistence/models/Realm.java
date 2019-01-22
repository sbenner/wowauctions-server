package com.heim.wowauctions.common.persistence.models;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/20/14
 * Time: 7:36 PM
 */

import lombok.Data;
import org.apache.solr.client.solrj.beans.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.List;


@Data
@SolrDocument(collection = "realms")
public class Realm implements Comparable{
    @Id
    String id;

    @Field("type_s")
    private String type;
    @Field("population_i")
    private int population;
    @Field("status_s")
    private String status;
    @Field("name_s")
    private String name;
    @Field("slug_s")
    private String slug;
    @Field("battlegroup_s")
    private String battlegroup;
    @Field("locale_s")
    private String locale;
    @Field("tz_s")
    private String timezone;

    @Indexed(name = "connected_realms", type = "text_general")
    private List<String> connectedRealms;

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        Realm other = (Realm) obj;
        return slug != null && slug.equals(other.getSlug());

    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((slug == null) ? 0 : slug.hashCode());
        return result;
    }

    @Override
    public int compareTo(Object o) {
        return ((Realm)o).getSlug().compareTo(getSlug());
    }
}
