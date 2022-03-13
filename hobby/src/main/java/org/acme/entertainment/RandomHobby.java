package org.acme.entertainment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public non-sealed class RandomHobby extends BasicHobby {
    private String cloudId;
    private String workerId;
    private boolean precious;

    public RandomHobby(String key, String activity, String type, int participants, double price) {
        super(key, activity, type, participants, price);
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
