package com.jwyoon.oauth.oauth;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.error.DefaultWebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.jwyoon.oauth.error.PasswordNotMatchException;
import com.jwyoon.oauth.service.UserDetailServiceImpl;

/**
 * @author user Oauth Server
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailServiceImpl userDetailsService;
        
    @Autowired
    private PasswordEncoders passwordEncoder;
    
    @Bean
    public TokenStore tokenStore() {
    	return new JwtTokenStore(accessTokenConverter());    	
    }
    
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
    	JwtAccessTokenConverter jwt = new JwtAccessTokenConverter();
    	jwt.setSigningKey("testjwtjwyoon");
    	return jwt;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenService() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());        
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setClientDetailsService(clientDetailsService);
        return defaultTokenServices;
    }

    @Bean
    @Primary
    public JdbcClientDetailsService JdbcClientDetailsService(DataSource dataSource) {
    	JdbcClientDetailsService jdbcClient = new JdbcClientDetailsService(dataSource);
    	jdbcClient.setPasswordEncoder(passwordEncoder);
        return jdbcClient;
    }

    @Autowired
    private ClientDetailsService clientDetailsService;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // OAuth2 인증서버 자체의 보안 정보를 설정하는 부분
        security.tokenKeyAccess("permitAll()").checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // Client 에 대한 정보를 설정하는 부분
        clients.withClientDetails(clientDetailsService);
    }

    @Bean
    protected AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }
            
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
// OAuth2 서버가 작동하기 위한 Endpoint에 대한 정보를 설정    	
        endpoints.authenticationManager(authenticationManager)
        .tokenStore(tokenStore()).accessTokenConverter(accessTokenConverter());
        endpoints.userDetailsService(userDetailsService);
        endpoints.exceptionTranslator(authorizationWebResponseExceptionTranslator());
        endpoints.authorizationCodeServices(authorizationCodeServices());
        endpoints.reuseRefreshTokens(false);        
        endpoints.allowedTokenEndpointRequestMethods(HttpMethod.GET, HttpMethod.POST,HttpMethod.PUT,HttpMethod.DELETE);
    }
    
    @SuppressWarnings("rawtypes")
	public WebResponseExceptionTranslator authorizationWebResponseExceptionTranslator() {
        return new DefaultWebResponseExceptionTranslator() {

            @Override
            public ResponseEntity<OAuth2Exception> translate(Exception e) throws Exception {
                Map responseMap = new HashMap();
                if(e instanceof PasswordNotMatchException) responseMap.put("code", ((PasswordNotMatchException) e).getHttpStatus().getReasonPhrase()); 
                responseMap.put("message", e.getMessage());
                return new ResponseEntity(responseMap, HttpStatus.UNAUTHORIZED);
            }
        };
    }
}