package org.icgc_argo.workflow.search.model.graphql;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Map;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming()
@JsonIgnoreProperties(ignoreUnknown = true)
public class GqlTask {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private String runId;

  private String sessionId;

  private Integer taskId;

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

  /** The cpus number request for the task execution */
  private Integer cpus;

  /** The memory request for the task execution */
  private Long memory;

  /** Time elapsed to complete since the submission */
  private Long duration;

  /** Task execution time i.e. delta between completion and start timestamp */
  private Long realtime;

  /** Real memory (resident set) size of the process. Equivalent to `ps -o rss` */
  private Long rss;

  /** Peak of real memory. This data is read from field `VmHWM` in `/proc/$pid/status file` */
  private Long peakRss;

  /** Virtual memory size of the process. Equivalent to `ps -o vsize` */
  private Long vmem;

  /** Peak of virtual memory. This data is read from field `VmPeak` in `/proc/$pid/status file` */
  private Long peakVmem;

  /**
   * Number of bytes the process directly read from disk. This data is read from file
   * `/proc/$pid/io`
   */
  private Long readBytes;

  /**
   * Number of bytes the process originally dirtied in the page-cache (assuming they will go to disk
   * later). This data is read from file `/proc/$pid/io`
   */
  private Long writeBytes;

  @SneakyThrows
  public static GqlTask parse(@NonNull Map<String, Object> sourceMap) {
    return MAPPER.convertValue(sourceMap, GqlTask.class);
  }
}
