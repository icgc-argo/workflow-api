package org.icgc_argo.workflow.search.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;

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
  public Docket productApi(SwaggerProperties properties) {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("org.icgc_argo.workflow.search.controller"))
        .build()
        .host(properties.host)
        .pathProvider(
            new RelativePathProvider(null) {
              @Override
              public String getApplicationBasePath() {
                return properties.getBaseUrl();
              }
            })
        .apiInfo(apiInfo());
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
