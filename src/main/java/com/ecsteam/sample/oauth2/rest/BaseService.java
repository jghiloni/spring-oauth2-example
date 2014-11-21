package com.ecsteam.sample.oauth2.rest;

import java.security.Principal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ecsteam.sample.oauth2.rest.model.Message;

@RestController
public class BaseService {

	@RequestMapping("/service/base/{pathVar}")
	public Object baseService(@PathVariable("pathVar") String pathVar, Principal principal,
			@RequestHeader("Authorization") String authorization) {
		StringBuilder builder = new StringBuilder("Hello, ");
		builder.append((principal == null || principal.getName() == null) ? "Anonymous" : principal.getName());
		builder.append(", from the base service, with path variable: ").append(pathVar);

		System.out.println(builder.toString());

		RestTemplate client = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authorization);

		HttpEntity<String> headerEntity = new HttpEntity<String>(headers);

		ResponseEntity<Message> subMessageEntity = client.exchange("http://localhost:8080/service/sub/{pathVar}",
				HttpMethod.GET, headerEntity, Message.class, Collections.singletonMap("pathVar", pathVar));

		Message subMessage = subMessageEntity.getBody();

		Map<String, Boolean> returnMaps = new LinkedHashMap<String, Boolean>(2);

		returnMaps.put("subExecution", subMessage.isSuccessfulExecution());
		returnMaps.put("baseExecution", Boolean.TRUE);

		return returnMaps;
	}
}
