package org.acme.entertainment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.json.bind.annotation.JsonbCreator;

@JsonIgnoreProperties(ignoreUnknown = true)
public sealed class BasicHobby permits AccessibleHobby, RandomHobby {

    private String key;
    private String activity;
    private String type;
    private int participants;
    private double price;

    BasicHobby() {
    }

    BasicHobby(String key, String activity, String type, int participants, double price) {
        super();
        this.key = key;
        this.activity = activity;
        this.type = type;
        this.participants = participants;
        this.price = price;
    }

    @JsonbCreator
    public static BasicHobby empty(String key, String activity, String type, int participants, double price) {
        return new BasicHobby(key, activity, type, participants, price);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getParticipants() {
        return participants;
    }

    public void setParticipants(int participants) {
        this.participants = participants;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
