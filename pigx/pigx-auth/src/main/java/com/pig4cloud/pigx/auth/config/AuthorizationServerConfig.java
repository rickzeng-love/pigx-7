/*
 *
 *      Copyright (c) 2018-2025, lengleng All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice,
 *  this list of conditions and the following disclaimer.
 *  Redistributions in binary form must reproduce the above copyright
 *  notice, this list of conditions and the following disclaimer in the
 *  documentation and/or other materials provided with the distribution.
 *  Neither the name of the pig4cloud.com developer nor the names of its
 *  contributors may be used to endorse or promote products derived from
 *  this software without specific prior written permission.
 *  Author: lengleng (wangiegie@gmail.com)
 *
 */

package com.pig4cloud.pigx.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pig4cloud.pigx.common.security.component.PigxCommenceAuthExceptionEntryPoint;
import com.pig4cloud.pigx.common.security.component.PigxWebResponseExceptionTranslator;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author lengleng
 * @date 2018/6/22 认证服务器配置
 */
@Configuration
@AllArgsConstructor
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	private final ClientDetailsService pigxClientDetailsServiceImpl;

	private final AuthenticationManager authenticationManagerBean;

	private final UserDetailsService pigxUserDetailsService;

	private final AuthorizationCodeServices authorizationCodeServices;

	private final TokenStore redisTokenStore;

	private final TokenEnhancer tokenEnhancer;

	private final ObjectMapper objectMapper;

	@Override
	@SneakyThrows
	public void configure(ClientDetailsServiceConfigurer clients) {
		clients.withClientDetails(pigxClientDetailsServiceImpl);
	}

	@Override
	public void configure(AuthorizationServerSecurityConfigurer oauthServer) {
		oauthServer.allowFormAuthenticationForClients()
				.authenticationEntryPoint(new PigxCommenceAuthExceptionEntryPoint(objectMapper))
				.checkTokenAccess("isAuthenticated()");
	}

	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
		endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST).tokenStore(redisTokenStore)
				.tokenEnhancer(tokenEnhancer).userDetailsService(pigxUserDetailsService)
				.authorizationCodeServices(authorizationCodeServices).authenticationManager(authenticationManagerBean)
				.reuseRefreshTokens(false).pathMapping("/oauth/confirm_access", "/token/confirm_access")
				.exceptionTranslator(new PigxWebResponseExceptionTranslator());
	}

}
