package org.icgc_argo.workflow.search.model.graphql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.*;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Run {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private String runId;

  private String runName;

  private String startTime;

  private String state;

  private Boolean success;

  private Integer exitStatus;

  private String errorReport;

  private String duration;

  private String completeTime;

  private String commandLine;

  private EngineParameters engineParameters;

  private Object parameters;

  @Data
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static final class EngineParameters {
    @JsonProperty("launch_dir")
    private String launchDir;

    @JsonProperty("project_dir")
    private String projectDir;

    private String resume;
    private String revision;

    @JsonProperty("work_dir")
    private String workDir;
  }

  @SneakyThrows
  public static Run parse(@NonNull Map<String, Object> sourceMap) {
    return MAPPER.convertValue(sourceMap, Run.class);
  }
}
