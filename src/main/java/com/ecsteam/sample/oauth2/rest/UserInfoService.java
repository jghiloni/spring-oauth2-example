package com.ecsteam.sample.oauth2.rest;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import org.springframework.cloud.security.sso.EnableOAuth2Sso;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@EnableOAuth2Sso
public class UserInfoService {

	@RequestMapping("/userinfo")
	public Map<String, String> getUserInfo(Principal principal) {
		return Collections.singletonMap("user_id", principal.getName());
	}
}
