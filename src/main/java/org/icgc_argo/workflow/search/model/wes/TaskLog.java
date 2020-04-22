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
public class TaskLog {

  private Integer taskId;

  private String name;

  private String process;

  private String tag;

  private String container;

  private Integer attempt;

  private State state;

  @Valid private List<String> cmd;

  private String submitTime;

  private String startTime;

  private String endTime;

  private String stdout;

  private String stderr;

  private Integer exitCode;

  private String workdir;

  private Integer cpus;

  private Integer memory;

  private Integer duration;

  private Integer realtime;
}
