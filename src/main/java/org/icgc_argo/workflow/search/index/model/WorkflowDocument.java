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

package org.icgc_argo.workflow.search.index.model;

import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class WorkflowDocument {

  /** Workflow run ID */
  @NonNull private String runId;

  /** Workflow session ID */
  @NonNull private String sessionId;

  /** The repository url */
  @NonNull private String repository;

  /** The overall state of the workflow run, mapped to WorkflowEvent - event */
  @NonNull private String state;

  @NonNull private Map<String, Object> parameters;

  /** When the command started executing */
  @NonNull private Instant startTime;

  /**
   * When the command stopped executing (completed, failed, or cancelled), completeTime does not
   * exist in ES when workflow is unfinished.
   */
  private Instant completeTime;

  /** Did the workflow succeed */
  @NonNull private Boolean success;

  /** Exit code of the program */
  @NonNull private Integer exitStatus;

  /** A URL to retrieve standard error logs of the workflow run or task */
  private String errorReport;

  /** Workflow duration */
  private Long duration;

  /** The command line that was executed */
  @NonNull private String commandLine;

  private Map<String, Object> engineParameters;
}
