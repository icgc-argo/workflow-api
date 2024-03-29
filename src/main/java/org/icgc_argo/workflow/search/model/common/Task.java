/*
 * Copyright (c) 2020 The Ontario Institute for Cancer Research. All rights reserved
 *
 * This program and the accompanying materials are made available under the terms of the GNU Affero General Public License v3.0.
 * You should have received a copy of the GNU Affero General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.icgc_argo.workflow.search.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.icgc_argo.workflow.search.model.graphql.GqlTask;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Task {

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

  public static Task fromGqlTask(GqlTask task) {
    return Task.builder()
        .runId(task.getRunId())
        .sessionId(task.getSessionId())
        .taskId(task.getTaskId())
        .name(task.getName())
        .process(task.getProcess())
        .state(task.getState())
        .tag(task.getTag())
        .container(task.getContainer())
        .attempt(task.getAttempt())
        .submitTime(task.getSubmitTime())
        .startTime(task.getStartTime())
        .completeTime(task.getCompleteTime())
        .exit(task.getExit())
        .script(task.getScript())
        .workdir(task.getWorkdir())
        .cpus(task.getCpus())
        .memory(task.getMemory())
        .duration(task.getDuration())
        .realtime(task.getRealtime())
        .rss(task.getRss())
        .peakRss(task.getPeakRss())
        .vmem(task.getVmem())
        .peakVmem(task.getVmem())
        .readBytes(task.getReadBytes())
        .writeBytes(task.getWriteBytes())
        .build();
  }
}
