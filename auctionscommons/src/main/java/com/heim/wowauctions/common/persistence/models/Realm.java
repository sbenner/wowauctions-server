package com.heim.wowauctions.common.persistence.models;

/**
 * Created with IntelliJ IDEA.
 * User: sbenner
 * Date: 8/20/14
 * Time: 7:36 PM
 */

import java.util.Set;


public class Realm {

    private String type;
    private int population;
    private boolean status;
    private String name;
    private String slug;
    private String battlegroup;
    private String locale;
    private String timezone;

    private Set<String> connectedRealms;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getBattlegroup() {
        return battlegroup;
    }

    public void setBattlegroup(String battlegroup) {
        this.battlegroup = battlegroup;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }


    public Set<String> getConnectedRealms() {
        return connectedRealms;
    }

    public void setConnectedRealms(Set<String> connectedRealms) {
        this.connectedRealms = connectedRealms;
    }

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
}
