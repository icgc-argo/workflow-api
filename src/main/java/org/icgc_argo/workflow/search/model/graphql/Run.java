package org.icgc_argo.workflow.search.model.graphql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Map;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Run {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private String runId;

  private String runName;

  private String repository;

  private String state;

  private Object parameters;

  private String startTime;

  private String completeTime;

  private Boolean success;

  private Integer exitStatus;

  private String errorReport;

  private String duration;

  private String commandLine;

  private EngineParameters engineParameters;

  @SneakyThrows
  public static Run parse(@NonNull Map<String, Object> sourceMap) {
    return MAPPER.convertValue(sourceMap, Run.class);
  }

  @Data
  @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static final class EngineParameters {
    private String launchDir;
    private String projectDir;
    private String resume;
    private String revision;
    private String workDir;
  }
}
