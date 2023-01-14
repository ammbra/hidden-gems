package org.acme.entertainment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import javax.json.bind.annotation.JsonbCreator;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public non-sealed class RandomHobby extends BasicHobby {
    private String cloudId;
    private String workerId;
    private boolean precious;

    private RandomHobby() {
        super();
    }

    private RandomHobby(String key, String activity, String type, int participants, double price, String cloudId, String workerId, boolean precious) {
        super(key, activity, type, participants, price);
        this.cloudId = cloudId;
        this.workerId = workerId;
        this.precious = precious;
    }

    @JsonbCreator
    public static RandomHobby empty(String key, String activity, String type, int participants, double price) {
        return new RandomHobby(key, activity, type, participants, price, null, null, false);
    }
    public String getCloudId() {
        return cloudId;
    }

    public void setCloudId(String cloudId) {
        this.cloudId = cloudId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public Boolean getPrecious() {
        return precious;
    }

    public void setPrecious(Boolean precious) {
        this.precious = precious;
    }
}
