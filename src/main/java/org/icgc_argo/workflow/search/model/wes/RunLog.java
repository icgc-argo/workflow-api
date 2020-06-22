package org.icgc_argo.workflow.search.model.wes;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class RunLog {

  private String runId;

  @Valid private List<String> cmd;

  private String startTime;

  private String endTime;

  private String stdout;

  private String stderr;

  private Integer exitCode;

  private Boolean success;

  private Integer duration;
}
