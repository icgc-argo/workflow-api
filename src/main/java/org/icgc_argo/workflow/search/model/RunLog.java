package org.icgc_argo.workflow.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import java.util.List;
import javax.validation.Valid;
import lombok.*;

/** Log and other info */
@ApiModel(description = "Log and other info")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RunLog {

  @JsonProperty("name")
  private String name;

  @JsonProperty("cmd")
  @Valid
  private List<String> cmd;

  @JsonProperty("start_time")
  private String startTime;

  @JsonProperty("end_time")
  private String endTime;

  @JsonProperty("stdout")
  private String stdout;

  @JsonProperty("stderr")
  private String stderr;

  @JsonProperty("exit_code")
  private Integer exitCode;

  @JsonProperty("success")
  private Boolean success;

  @JsonProperty("duration")
  private Integer duration;
}
