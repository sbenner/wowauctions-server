package com.heim.wowauctions.common.persistence.models;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/20/14
 * Time: 7:36 PM
 */

import lombok.Data;

import java.util.Set;

@Data
public class Realm implements Comparable{

    private int id;
    private String type;
    private int population;
    private boolean status;
    private String name;
    private String slug;
    private String battlegroup;
    private String locale;
    private String timezone;
    private String connectedRealmUrl;
    private Set<String> connectedRealms;

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
