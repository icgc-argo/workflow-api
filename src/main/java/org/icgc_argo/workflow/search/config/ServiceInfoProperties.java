package org.icgc_argo.workflow.search.config;

import lombok.Data;
import org.icgc_argo.workflow.search.model.wes.DefaultWorkflowEngineParameter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

@Data
@Configuration
@ConfigurationProperties(prefix = "service-info")
public class ServiceInfoProperties {

  /**
   * A web page URL with human-readable instructions on how to get an authorization token for use
   * with a specific WES endpoint.
   */
  private String authInstructionsUrl;

  /**
   * An email address URL (mailto:) or web page URL with contact information for the operator of a
   * specific WES endpoint. Users of the endpoint should use this to report problems or security
   * vulnerabilities.
   */
  private String contactInfoUrl;

  /**
   * The filesystem protocols supported by this service, currently these may include common
   * protocols using the terms 'http', 'https', 'sftp', 's3', 'gs', 'file', or 'synapse', but others
   * are possible and the terms beyond these core protocols are currently not fixed. This section
   * reports those protocols (either common or not) supported by this WES service.
   */
  private List<String> supportedFilesystemProtocols;

  /** The version(s) of the WES schema supported by this service */
  private List<String> supportedWesVersions;

  /**
   * The workflow descriptor type, must be "Nextflow" currently (or another alternative supported by
   * this WES instance)
   */
  private String workflowType;

  /**
   * A map with keys as the workflow format type name (currently only Nextflow is used although a
   * service may support others) and value is a workflow_type_version object which simply contains
   * an array of one or more version strings
   */
  private Map<String, String> workflowTypeVersions;

  /**
   * The engine(s) used by this WES service, key is engine name (e.g. Nextflow) and value is version
   */
  private Map<String, String> workflowEngineVersions;

  /**
   * Each workflow engine can present additional parameters that can be sent to the workflow engine.
   * This message will list the default values, and their types for each workflow engine.
   */
  private List<DefaultWorkflowEngineParameter> defaultWorkflowEngineParameters;
}
