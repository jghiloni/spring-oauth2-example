package com.ecsteam.sample.oauth2.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.inMemoryAuthentication().withUser("josh").password("Passw0rd")
				.roles("USER").and().withUser("steve").password("Welcome1")
				.roles("USER");
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/webjars/**", "/images/**",
				"/oauth/uncache_approvals", "/oauth/cache_approvals");
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// @formatter:off
        http
            .authorizeRequests().antMatchers("/login.jsp").permitAll().and()
            .authorizeRequests()
                .anyRequest().hasRole("USER")
                .and()
            .exceptionHandling()
                .accessDeniedPage("/login.jsp?authorization_error=true")
                .and()
            // TODO: put CSRF protection back into this endpoint
            .csrf()
                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize")).disable()
            .logout()
                .logoutSuccessUrl("/index.jsp")
                .logoutUrl("/logout.do")
                .and()
            .httpBasic()
            	.and()
            .formLogin()
                    .usernameParameter("j_username")
                    .passwordParameter("j_password")
                    .failureUrl("/login.jsp?authentication_error=true")
                    .loginPage("/login.jsp")
                    .loginProcessingUrl("/login.do");
        // @formatter:on
	}
}
