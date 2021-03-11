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

package org.icgc_argo.workflow.search.config;

import static java.util.Collections.singletonList;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.paths.DefaultPathProvider;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.List;

import static java.util.Collections.singletonList;

@Configuration
public class SwaggerConfig {

  @Value("${spring.application.name}")
  private String appName;

  @Value("${spring.application.description}")
  private String appDescription;

  @Value("${spring.application.version}")
  private String appVersion;

  ApiInfo apiInfo() {
    return new ApiInfoBuilder()
        .title(appName)
        .description(appDescription)
        .version(appVersion)
        .build();
  }

  @Bean
  public Docket api(SwaggerProperties properties) {
    return new Docket(DocumentationType.SWAGGER_2)
        .securityContexts(securityContexts())
        .securitySchemes(securitySchemes())
        .select()
        .apis(RequestHandlerSelectors.basePackage("org.icgc_argo.workflow.search.controller"))
        .build()
        .host(properties.host)
        .pathProvider(
            new DefaultPathProvider() {
              @Override
              public String getDocumentationPath() {
                return properties.getBaseUrl();
              }
            })
        .apiInfo(apiInfo());
  }
  private List<SecurityScheme> securitySchemes() {
    return singletonList(new ApiKey("JWT", "Authorization", "header"));
  }

  private List<SecurityContext> securityContexts() {
    AuthorizationScope[] authorizationScopes = { new AuthorizationScope("global", "accessEverything")};
    val securityRefs = singletonList(new SecurityReference("JWT", authorizationScopes));
    return List.of(SecurityContext.builder().securityReferences(securityRefs).build());
  }

  private List<SecurityScheme> securitySchemes() {
    return singletonList(new ApiKey("JWT", "Authorization", "header"));
  }

  private List<SecurityContext> securityContexts() {
    AuthorizationScope[] authorizationScopes = {
      new AuthorizationScope("global", "accessEverything")
    };
    val securityRefs = singletonList(new SecurityReference("JWT", authorizationScopes));
    return List.of(SecurityContext.builder().securityReferences(securityRefs).build());
  }

  @Component
  @ConfigurationProperties(prefix = "swagger")
  class SwaggerProperties {
    /** Specify host if Wrokflwo Search is running behind proxy. */
    @Setter @Getter private String host = "";

    /**
     * If there is url write rule, you may want to set this variable. This value requires host to be
     * not empty.
     */
    @Setter @Getter private String baseUrl = "";
  }
}
