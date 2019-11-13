package org.icgc_argo.workflow.search.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;

@javax.annotation.Generated(
    value = "io.swagger.codegen.languages.SpringCodegen",
    date = "2019-11-01T10:34:43.963-04:00")
@Configuration
public class SwaggerConfig {

  ApiInfo apiInfo() {
    return new ApiInfo(
        "Workflow Search",
        "Workflow Search API Documentation",
        "0.0.1",
        "",
        "contact@overture.bio",
        "",
        "");
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
    /** Specify host if ego is running behind proxy. */
    @Setter @Getter private String host = "";

    /**
     * If there is url write rule, you may want to set this variable. This value requires host to be
     * not empty.
     */
    @Setter @Getter private String baseUrl = "";
  }
}
