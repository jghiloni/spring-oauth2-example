package com.ecsteam.sample.oauth2.rest;

import java.security.Principal;
import java.util.Collections;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubService {

	@RequestMapping("/service/sub/{pathVar}")
	public Object baseService(@PathVariable("pathVar") String pathVar,
			Principal principal) {

		StringBuilder builder = new StringBuilder("Hello, ");
		builder.append((principal == null || principal.getName() == null) ? "Anonymous"
				: principal.getName());
		builder.append(", from the sub service, with path variable: ").append(
				pathVar);

		System.out.println(builder.toString());

		return Collections.singletonMap("successfulExecution", Boolean.TRUE);
	}
}
