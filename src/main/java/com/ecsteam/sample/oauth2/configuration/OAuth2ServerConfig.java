package com.ecsteam.sample.oauth2.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;

import com.ecsteam.sample.oauth2.configuration.util.SampleApprovalHandler;

/**
 * @author Rob Winch
 * 
 */
@Configuration
public class OAuth2ServerConfig {

	private static final String OAUTH_RESOURCE_ID = "ecsoauth";

	@Configuration
	@Order(10)
	protected static class UiResourceConfiguration extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http.requestMatchers().antMatchers("/service/**", "/obo/**", "/me").and().authorizeRequests()
					.antMatchers("/service/**").access("hasRole('ROLE_USER')").and().authorizeRequests()
					.antMatchers("/obo/**").access("hasRole('ROLE_USER') and hasRole('ROLE_IMPERSONATOR')");
			// @formatter:on
		}
	}

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

		@Override
		public void configure(ResourceServerSecurityConfigurer resources) {
			resources.resourceId(OAUTH_RESOURCE_ID);
		}

		@Override
		public void configure(HttpSecurity http) throws Exception {
			// @formatter:off
			http.requestMatchers()
					.antMatchers("/service/**", "/obo/**", "/oauth/users/**", "/oauth/clients/**", "/me")
					.and()
					.authorizeRequests()
					.antMatchers("/me")
					.access("#oauth2.hasScope('read')")
					.antMatchers("/obo/**")
					.access("hasRole('ROLE_IMPERSONATOR')")
					.antMatchers("/service/**")
					.access("#oauth2.hasScope('read') or hasRole('ROLE_USER')")
					.regexMatchers(HttpMethod.DELETE, "/oauth/users/([^/].*?)/tokens/.*")
					.access("#oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('write')")
					.regexMatchers(HttpMethod.GET, "/oauth/clients/([^/].*?)/users/.*")
					.access("#oauth2.clientHasRole('ROLE_CLIENT') and (hasRole('ROLE_USER') or #oauth2.isClient()) and #oauth2.hasScope('read')")
					.regexMatchers(HttpMethod.GET, "/oauth/clients/.*")
					.access("#oauth2.clientHasRole('ROLE_CLIENT') and #oauth2.isClient() and #oauth2.hasScope('read')");
			// @formatter:on
		}

	}

	@Configuration
	@EnableAuthorizationServer
	protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private TokenStore tokenStore;

		@Autowired
		private UserApprovalHandler userApprovalHandler;

		@Autowired
		@Qualifier("authenticationManagerBean")
		private AuthenticationManager authenticationManager;

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

			// @formatter:off
			clients.inMemory().withClient("joshtest").resourceIds(OAUTH_RESOURCE_ID)
					.authorizedGrantTypes("password", "authorization_code").authorities("ROLE_CLIENT")
					.scopes("read", "write", "trust").secret("testsecret").autoApprove(true).and().withClient("tonr")
					.resourceIds(OAUTH_RESOURCE_ID).authorizedGrantTypes("authorization_code", "implicit")
					.authorities("ROLE_CLIENT").scopes("read", "write").secret("secret").and()
					.withClient("my-client-with-registered-redirect").resourceIds(OAUTH_RESOURCE_ID)
					.authorizedGrantTypes("authorization_code", "client_credentials").authorities("ROLE_CLIENT")
					.scopes("read", "trust").redirectUris("http://anywhere?key=value").and()
					.withClient("my-trusted-client")
					.authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
					.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT").scopes("read", "write", "trust")
					.accessTokenValiditySeconds(60).and().withClient("my-trusted-client-with-secret")
					.authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
					.authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT").scopes("read", "write", "trust")
					.secret("somesecret").and().withClient("my-less-trusted-client")
					.authorizedGrantTypes("authorization_code", "implicit").authorities("ROLE_CLIENT")
					.scopes("read", "write", "trust").and().withClient("my-less-trusted-autoapprove-client")
					.authorizedGrantTypes("implicit").authorities("ROLE_CLIENT").scopes("read", "write", "trust")
					.autoApprove(true);
			// @formatter:on
		}

		@Bean
		public TokenStore tokenStore() {
			return new InMemoryTokenStore();
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
			endpoints.tokenStore(tokenStore).userApprovalHandler(userApprovalHandler)
					.authenticationManager(authenticationManager);
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
			oauthServer.realm("ecsoauth/client");
		}

	}

	protected static class Stuff {

		@Autowired
		private ClientDetailsService clientDetailsService;

		@Autowired
		private TokenStore tokenStore;

		@Bean
		public ApprovalStore approvalStore() throws Exception {
			TokenApprovalStore store = new TokenApprovalStore();
			store.setTokenStore(tokenStore);
			return store;
		}

		@Bean
		@Lazy
		@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
		public SampleApprovalHandler userApprovalHandler() throws Exception {
			SampleApprovalHandler handler = new SampleApprovalHandler();
			handler.setApprovalStore(approvalStore());
			handler.setRequestFactory(new DefaultOAuth2RequestFactory(clientDetailsService));
			handler.setClientDetailsService(clientDetailsService);
			handler.setUseApprovalStore(true);
			return handler;
		}
	}

}
