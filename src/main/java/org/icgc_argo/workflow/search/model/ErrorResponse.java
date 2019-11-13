package org.icgc_argo.workflow.search.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;

/** An object that can optionally include information about the error. */
@ApiModel(description = "An object that can optionally include information about the error.")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ErrorResponse {

  @JsonProperty("msg")
  private String msg;

  @JsonProperty("status_code")
  private Integer statusCode;
}
