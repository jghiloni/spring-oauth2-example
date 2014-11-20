/*
 * Copyright 2002-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ecsteam.sample.oauth2.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * @author Rob Winch
 * @author Dave Syer
 *
 */
@Configuration
// @EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {
public class MethodSecurityConfig {
	@Autowired
	private SecurityConfig securityConfig;

//    @Override
//    protected MethodSecurityExpressionHandler createExpressionHandler() {
//        return new OAuth2MethodSecurityExpressionHandler();
//    }
}