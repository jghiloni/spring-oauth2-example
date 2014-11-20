package com.ecsteam.sample.oauth2.rest.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
	private boolean successfulExecution;

	public boolean isSuccessfulExecution() {
		return successfulExecution;
	}
}
