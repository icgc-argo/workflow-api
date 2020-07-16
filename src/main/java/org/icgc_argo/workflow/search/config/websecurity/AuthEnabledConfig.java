package org.icgc_argo.workflow.search.config.websecurity;

import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@EnableWebSecurity
@Slf4j
@Profile("secure")
public class AuthEnabledConfig extends WebSecurityConfigurerAdapter {

    AuthProperties authProperties;

    ResourceLoader resourceLoader;

    @Autowired
    public AuthEnabledConfig(AuthProperties authProperties, ResourceLoader resourceLoader) {
        this.authProperties = authProperties;
        this.resourceLoader = resourceLoader;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/graphql/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
            .and()
                .oauth2ResourceServer().jwt()
                .decoder(jwtDecoder())
                .jwtAuthenticationConverter(grantedAuthoritiesExtractor());
    }

    private Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(this.jwtToGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    private final Converter<Jwt, Collection<GrantedAuthority>> jwtToGrantedAuthoritiesConverter = (jwt) -> {
        val scopesBuilder = ImmutableList.<String>builder();

        try {
            val context = (Map<String, Object>) jwt.getClaims().get("context");
            scopesBuilder.addAll((Collection<String>) context.get("scope"));
        } catch (Exception e) {
            log.error("Unable to extract scopes from JWT");
        }

        val scopes = scopesBuilder.build();

        log.info("JWT scopes: " + scopes);

        return scopes.stream()
                       .map(SimpleGrantedAuthority::new)
                       .collect(toList());
    };

    @SneakyThrows
    private JwtDecoder jwtDecoder() {
        String publicKeyStr;

        val publicKeyUrl = authProperties.getJwtPublicKeyUrl();
        if (publicKeyUrl != null && !publicKeyUrl.isEmpty()) {
            publicKeyStr = fetchJWTPublicKey(publicKeyUrl);
        } else {
            publicKeyStr = authProperties.getJwtPublicKeyStr();
        }

        val publicKeyContent = publicKeyStr
                                       .replaceAll("\\n", "")
                                       .replace("-----BEGIN PUBLIC KEY-----", "")
                                       .replace("-----END PUBLIC KEY-----", "");

        KeyFactory kf = KeyFactory.getInstance("RSA");

        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyContent));
        RSAPublicKey publicKey = (RSAPublicKey) kf.generatePublic(keySpecX509);

        return NimbusJwtDecoder.withPublicKey(publicKey).build();

    }

    /**
     * Call EGO server for public key to use when verifying JWTs
     */
    @SneakyThrows
    private String fetchJWTPublicKey(String publicKeyUrl) {
        log.info("Fetching EGO public key");
        val publicKeyResource = resourceLoader.getResource(publicKeyUrl);

        val stringBuilder = new StringBuilder();
        val reader = new BufferedReader(
                new InputStreamReader(publicKeyResource.getInputStream()));

        reader.lines().forEach(stringBuilder::append);
        return stringBuilder.toString();
    }
}
