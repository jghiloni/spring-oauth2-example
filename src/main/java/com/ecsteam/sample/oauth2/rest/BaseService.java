package com.ecsteam.sample.oauth2.rest;

import java.security.Principal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.security.sso.EnableOAuth2Sso;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@EnableOAuth2Sso
public class BaseService {
	
	@Value("${demoapp.url:http://localhost:8080}")
	private String appUrl;

	@RequestMapping("/service/base/{pathVar}")
	public Object baseService(@PathVariable("pathVar") String pathVar, Principal principal,
			@RequestHeader("Authorization") String authorization) {
		StringBuilder builder = new StringBuilder("Hello, ");
		builder.append((principal == null || principal.getName() == null) ? "Anonymous" : principal.getName());
		builder.append(", from the base service, with path variable: ").append(pathVar);

		//System.out.println(builder.toString());

		RestTemplate client = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authorization);

		HttpEntity<String> headerEntity = new HttpEntity<String>(headers);

		Map<String, String> substitutions = new LinkedHashMap<String, String>();
		substitutions.put("baseUrl", appUrl);
		substitutions.put("pathVar", pathVar);
		
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> subMessageEntity = client.exchange("{baseUrl}/service/sub/{pathVar}",
				HttpMethod.GET, headerEntity, Map.class, substitutions);

		Map<?,?> subMessage = subMessageEntity.getBody();

		Map<String, Object> returnMaps = new LinkedHashMap<String, Object>(3);

		returnMaps.put("execution", Boolean.TRUE);
		returnMaps.put("message", builder.toString());
		returnMaps.put("subService", subMessage);
		return returnMaps;
	}
}
