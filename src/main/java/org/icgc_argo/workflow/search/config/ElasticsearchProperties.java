package org.icgc_argo.workflow.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "elastic")
public class ElasticsearchProperties {
  String host;
  Integer port;
  String workflowIndex;
  String taskIndex;
  String username;
  String password;
  Boolean useAuthentication;
}
