package com.ecsteam.sample.oauth2.rest;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestOperations;

public class SubServiceController {
	private RestOperations restTemplate;

	private String baseUri;

	private String urlPattern;

	public Map<?, ?> executeSubService(String pathVariable) {
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> entity = restTemplate.getForEntity(urlPattern, Map.class, baseUri, pathVariable);
		
		return entity.getBody();
	}

	public void setRestTemplate(RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setBaseUri(String baseUri) {
		this.baseUri = baseUri;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
	}
}
