package org.icgc_argo.workflow.search.config;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.icgc_argo.workflow.search.model.DefaultWorkflowEngineParameter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "service-info")
public class ServiceInfoProperties {

  private String authInstructionsUrl;
  private String contactInfoUrl;
  private List<String> supportedFilesystemProtocols;
  private List<String> supportedWesVersions;
  private String workflowType;
  private Map<String, String> workflowEngineVersions;
  private Map<String, String> workflowTypeVersions;
  private List<DefaultWorkflowEngineParameter> defaultWorkflowEngineParameters;
}
