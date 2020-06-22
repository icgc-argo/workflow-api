package org.icgc_argo.workflow.search.model.wes;

import io.swagger.annotations.ApiModel;
import java.util.List;
import lombok.*;

/** The service will return a RunListResponse when receiving a successful RunListRequest. */
@ApiModel(
    description =
        "The service will return a RunListResponse when receiving a successful RunListRequest.")
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class  RunListResponse {

  @NonNull private List<RunStatus> runs;

  private String nextPageToken;
}
