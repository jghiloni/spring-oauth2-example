package com.ecsteam.sample.oauth2.rest;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.cloud.security.resource.EnableOAuth2Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@EnableOAuth2Sso
@EnableOAuth2Resource
public class SubService {

	@RequestMapping("/service/sub/{pathVar}")
	public Object baseService(@PathVariable("pathVar") String pathVar, Principal principal) {

		StringBuilder builder = new StringBuilder("Hello, ");
		builder.append((principal == null || principal.getName() == null) ? "Anonymous" : principal.getName());
		builder.append(", from the sub service, with path variable: ").append(pathVar);
		
		Map<String, Object> returnObject = new LinkedHashMap<String, Object>();

		returnObject.put("message", builder.toString());
		returnObject.put("execution", Boolean.TRUE);
		
		return returnObject;
	}
}
