package com.ecsteam.sample.oauth2.rest;

import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecsteam.sample.oauth2.token.OBOTokenGranter;

@RestController
public class OBOTokenGenerator {

	@Autowired
	private AuthorizationServerTokenServices tokenServices;

	@Autowired
	private ClientDetailsService clientDetailsService;

	private OAuth2RequestFactory requestFactory = null;

	@RequestMapping("/obo/{user}")
	public ResponseEntity<OAuth2AccessToken> getOBOToken(Principal principal,
			@PathVariable("user") String user) {

		if (requestFactory == null) {
			requestFactory = new DefaultOAuth2RequestFactory(
					clientDetailsService);
		}

		OBOTokenGranter granter = new OBOTokenGranter(tokenServices,
				clientDetailsService, requestFactory);

		String clientId = getClientId(principal);
		ClientDetails authenticatedClient = clientDetailsService
				.loadClientByClientId(clientId);

		Map<String, String> parameters = new LinkedHashMap<String, String>();
		parameters.put("username", user);

		TokenRequest tokenRequest = requestFactory.createTokenRequest(parameters, authenticatedClient);
		OAuth2AccessToken token = granter.grant(OBOTokenGranter.GRANT_TYPE,
				tokenRequest);

		return getResponse(token);
		// return null;
	}

	/**
	 * @param principal
	 *            the currently authentication principal
	 * @return a client id if there is one in the principal
	 */
	protected String getClientId(Principal principal) {
		Authentication client = (Authentication) principal;
		if (!client.isAuthenticated()) {
			throw new InsufficientAuthenticationException(
					"The client is not authenticated.");
		}
		String clientId = client.getName();
		if (client instanceof OAuth2Authentication) {
			// Might be a client and user combined authentication
			clientId = ((OAuth2Authentication) client).getOAuth2Request()
					.getClientId();
		}
		return clientId;
	}

	private ResponseEntity<OAuth2AccessToken> getResponse(
			OAuth2AccessToken accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Cache-Control", "no-store");
		headers.set("Pragma", "no-cache");
		return new ResponseEntity<OAuth2AccessToken>(accessToken, headers,
				HttpStatus.OK);
	}

}
