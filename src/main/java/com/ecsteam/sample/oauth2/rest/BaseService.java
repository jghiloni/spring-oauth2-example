package com.ecsteam.sample.oauth2.rest;

import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.cloud.security.sso.EnableOAuth2Sso;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@EnableOAuth2Sso
public class BaseService {

	private SubServiceController controller;

	@RequestMapping("/service/base/{pathVar}")
	public Object baseService(@PathVariable("pathVar") String pathVar, Principal principal) {
		StringBuilder builder = new StringBuilder("Hello, ");
		builder.append((principal == null || principal.getName() == null) ? "Anonymous" : principal.getName());
		builder.append(", from the base service, with path variable: ").append(pathVar);

		Map<?, ?> subMessage = controller.executeSubService(pathVar);

		Map<String, Object> returnMaps = new LinkedHashMap<String, Object>(3);

		returnMaps.put("execution", Boolean.TRUE);
		returnMaps.put("message", builder.toString());
		returnMaps.put("subService", subMessage);
		return returnMaps;
	}

	public void setSubServiceController(SubServiceController controller) {
		this.controller = controller;
	}
}
