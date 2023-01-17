package org.acme.entertainment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public sealed class BasicHobby permits RandomHobby {

	private String key;
	private String activity;
	private String type;

	public BasicHobby() {
	}

	public BasicHobby(String key, String activity, String type) {
		this.key = key;
		this.activity = activity;
		this.type = type;
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

}
