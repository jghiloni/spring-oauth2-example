package com.ecsteam.sample.oauth2.configuration;

import java.util.Arrays;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.client.RestOperations;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.ecsteam.sample.oauth2.rest.BaseService;
import com.ecsteam.sample.oauth2.rest.SubService;
import com.ecsteam.sample.oauth2.rest.SubServiceController;

@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

//	@Bean
//	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
//		return new PropertySourcesPlaceholderConfigurer();
//	}

	@Bean
	public ContentNegotiatingViewResolver contentViewResolver() throws Exception {
		ContentNegotiationManagerFactoryBean contentNegotiationManager = new ContentNegotiationManagerFactoryBean();
		contentNegotiationManager.addMediaType("json", MediaType.APPLICATION_JSON);

		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/jsp/");
		viewResolver.setSuffix(".jsp");

		MappingJackson2JsonView defaultView = new MappingJackson2JsonView();
		defaultView.setExtractValueFromSingleKeyModel(true);

		ContentNegotiatingViewResolver contentViewResolver = new ContentNegotiatingViewResolver();
		contentViewResolver.setContentNegotiationManager(contentNegotiationManager.getObject());
		contentViewResolver.setViewResolvers(Arrays.<ViewResolver> asList(viewResolver));
		contentViewResolver.setDefaultViews(Arrays.<View> asList(defaultView));
		return contentViewResolver;
	}

	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}
	
	@Bean
	public SubServiceController subServiceController(@Value("${demoapp.url:http://localhost:8080}") String appUrl,
			@Value("${demoapp.subservice.url:http://{baseUri}/service/sub/{pathVar}") String urlPattern,
			@Qualifier("demoTemplate") RestOperations restTemplate) {
		SubServiceController ssc = new SubServiceController();
		ssc.setBaseUri(appUrl);
		ssc.setUrlPattern(urlPattern);
		ssc.setRestTemplate(restTemplate);

		return ssc;
	}

	@Bean
	public BaseService baseService(SubServiceController controller) {
		BaseService service = new BaseService();
		service.setSubServiceController(controller);
		
		return service;
	}

	@Configuration
	@EnableOAuth2Client
	protected static class ResourceConfiguration {
		@Value("${oauth2.client.tokenUri}")
		private String tokenUri;

		@Value("${oauth2.client.authorizationUri}")
		private String authUri;

		@Resource
		@Qualifier("accessTokenRequest")
		private AccessTokenRequest accessTokenRequest;

//		@Bean(name="demoDetails")
		public OAuth2ProtectedResourceDetails demo() {
			AuthorizationCodeResourceDetails deets = new AuthorizationCodeResourceDetails();
			deets.setId("demo/demo");
			deets.setClientId("app");
			deets.setClientSecret("appclientsecret");
			deets.setAccessTokenUri(tokenUri);
			deets.setUserAuthorizationUri(authUri);
			deets.setScope(Arrays.asList("read", "write", "trust"));

			return deets;
		}

		@Bean
		@Scope(value = "session", proxyMode = ScopedProxyMode.INTERFACES)
		public OAuth2RestTemplate demoTemplate() {
			return new OAuth2RestTemplate(demo(), new DefaultOAuth2ClientContext(accessTokenRequest));
		}
	}
}
