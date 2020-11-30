/*
 * Copyright (c) 2020 The Ontario Institute for Cancer Research. All rights reserved
 *
 * This program and the accompanying materials are made available under the terms of the GNU Affero General Public License v3.0.
 * You should have received a copy of the GNU Affero General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.icgc_argo.workflow.search.config.websecurity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toUnmodifiableList;

@EnableWebSecurity
@Slf4j
@Profile("secure")
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AuthEnabledConfig extends WebSecurityConfigurerAdapter {

    private final AuthProperties authProperties;

    private final ResourceLoader resourceLoader;

    @Autowired
    public AuthEnabledConfig(AuthProperties authProperties, ResourceLoader resourceLoader) {
        this.authProperties = authProperties;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/graphql/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .antMatchers("/runs/**").permitAll()
                .antMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**").permitAll()
            .and()
                .authorizeRequests()
                .anyRequest().authenticated()
            .and()
                .oauth2ResourceServer().jwt()
                .decoder(jwtDecoder())
                .jwtAuthenticationConverter(grantedAuthoritiesExtractor());
    }

    @Bean
    public Function<Authentication, Boolean> queryScopeChecker() {
        val expectedScopes = Lists.newArrayList(
                Iterables.concat(
                        authProperties.getGraphqlScopes().getQueryOnly(),
                        authProperties.getGraphqlScopes().getQueryAndMutation()
                ));

        return authentication -> {
            val scopes =
                    authentication.getAuthorities().stream()
                            .map(Objects::toString)
                            .collect(toUnmodifiableList());

            val foundScopes =
                    scopes.stream().filter(expectedScopes::contains).collect(toUnmodifiableList());

            return foundScopes.size() > 0;
        };
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
