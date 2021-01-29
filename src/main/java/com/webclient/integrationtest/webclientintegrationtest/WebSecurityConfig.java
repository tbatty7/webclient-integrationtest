package com.webclient.integrationtest.webclientintegrationtest;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.BearerTokenError;

import static org.springframework.boot.autoconfigure.mongo.MongoProperties.DEFAULT_URI;
import static org.springframework.security.oauth2.jwt.JwtClaimNames.AUD;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    static JwtDecoder wrapJwtDecoderWithAudienceCheck(JwtDecoder jwtDecoder, String audience) {
        return token -> {
            Jwt jwt = jwtDecoder.decode(token);
            if (!jwt.containsClaim(AUD) || !jwt.getClaimAsStringList(AUD).contains(audience)) {
                String errorDescription = "Audience field does not match";
                throw new OAuth2AuthenticationException(new BearerTokenError("invalid_resource", HttpStatus.FORBIDDEN,
                        errorDescription, DEFAULT_URI));
            }
            return jwt;
        };
    }

    @Configuration
    public static class ResourceServerSecurityConfiguration extends WebSecurityConfigurerAdapter {

        String audience = "audience1234";

        private JwtDecoder jwtDecoderByIssuerUri;

        public ResourceServerSecurityConfiguration(JwtDecoder jwtDecoderByIssuerUri) {
            this.jwtDecoderByIssuerUri = jwtDecoderByIssuerUri;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            JwtDecoder newJwtDecoder = wrapJwtDecoderWithAudienceCheck(this.jwtDecoderByIssuerUri, audience);

            http
                    .csrf()
                    .disable()
                    .authorizeRequests()
                    .requestMatchers(EndpointRequest.to("health")).permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .oauth2ResourceServer()
                    .jwt()
                    .decoder(newJwtDecoder);
        }
    }

}
