package org.acme.entertainment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.json.bind.annotation.JsonbCreator;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class AccessibleHobby extends BasicHobby {
    private String link;
    private double accessibility;


    private AccessibleHobby(String key, String activity, String type, int participants, double price) {
        super(key, activity, type, participants, price);
    }

    @JsonbCreator
    public static AccessibleHobby empty(String key, String activity, String type, int participants, double price) {
        return new AccessibleHobby(key, activity, type, participants, price);
    }
    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public double getAccessibility() {
        return accessibility;
    }

    public void setAccessibility(double accessibility) {
        this.accessibility = accessibility;
    }
}


