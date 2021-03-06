package com.jwyoon.oauth.oauth;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
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
    	JwtTokenStore tokenStore= new MyJwtTokenStore(accessTokenConverter(),dataSource);
    	TokenApprovalStore approvalStore = new TokenApprovalStore();
    	approvalStore.setTokenStore(new JdbcTokenStore(dataSource));
        tokenStore.setApprovalStore(approvalStore); 
        return tokenStore;
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
    	JwtAccessTokenConverter jwt = new JwtAccessTokenConverter();
    	jwt.setSigningKey("jwt");
    	return jwt;
    }

    @Bean
    @Primary
    public DefaultTokenServices tokenService() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setAccessTokenValiditySeconds(1000 * 60 * 1);
        defaultTokenServices.setRefreshTokenValiditySeconds(1000 * 60);
        defaultTokenServices.setReuseRefreshToken(false);
        defaultTokenServices.setSupportRefreshToken(true);
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
        // OAuth2 ???????????? ????????? ?????? ????????? ???????????? ??????
        security.tokenKeyAccess("permitAll()").checkTokenAccess("permitAll()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        // Client ??? ?????? ????????? ???????????? ??????
        clients.withClientDetails(clientDetailsService);
    }

    @Bean
    protected AuthorizationCodeServices authorizationCodeServices() {
        return new JdbcAuthorizationCodeServices(dataSource);
    }
            
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
// OAuth2 ????????? ???????????? ?????? Endpoint??? ?????? ????????? ??????    	
        endpoints.authenticationManager(authenticationManager).tokenStore(tokenStore()).accessTokenConverter(accessTokenConverter());
        endpoints.userDetailsService(userDetailsService);
        endpoints.exceptionTranslator(authorizationWebResponseExceptionTranslator());
        endpoints.authorizationCodeServices(authorizationCodeServices());
        endpoints.reuseRefreshTokens(false);
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