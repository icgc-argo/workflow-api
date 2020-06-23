package org.icgc_argo.workflow.search.model.graphql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Task {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private String runId;

  private String sessionId;

  private String taskId;

  private String name;

  private String process;

  private String state;

  private String tag;

  private String container;

  private Integer attempt;

  private String submitTime;

  private String startTime;

  private String completeTime;

  private Integer exit;

  private String script;

  private String workdir;

  private Integer cpus;

  private Integer memory;

  private Integer duration;

  private Integer realtime;

  @SneakyThrows
  public static Task parse(@NonNull Map<String, Object> sourceMap) {
    return MAPPER.convertValue(sourceMap, Task.class);
  }
}
