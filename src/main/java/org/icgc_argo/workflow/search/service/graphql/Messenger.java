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

package org.icgc_argo.workflow.search.service.graphql;

import static org.icgc_argo.workflow.search.util.JacksonUtils.toJsonString;
import static org.icgc_argo.workflow.search.util.WesUtils.generateWesRunId;

import com.pivotal.rabbitmq.source.Sender;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.icgc_argo.workflow.search.model.common.RunRequest;
import org.icgc_argo.workflow.search.model.wes.RunId;
import org.icgc_argo.workflow.search.rabbitmq.schema.EngineParams;
import org.icgc_argo.workflow.search.rabbitmq.schema.RunState;
import org.icgc_argo.workflow.search.rabbitmq.schema.WfMgmtRunMsg;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class Messenger {

  private final Sender<WfMgmtRunMsg> sender;

  public Mono<RunId> run(RunRequest runRequest) {
    val runId = generateWesRunId();
    val msg = createWfMgmtRunMsg(runId, runRequest, RunState.QUEUED);
    return Mono.just(msg).flatMap(sender::send).map(o -> new RunId(runId));
  }

  public Mono<RunId> cancel(String runId) {
    val event = createWfMgmtRunMsg(runId, RunState.CANCELING);
    return Mono.just(event).flatMap(sender::send).map(o -> new RunId(runId));
  }

  private static WfMgmtRunMsg createWfMgmtRunMsg(
      String runId, RunRequest runRequest, RunState state) {

    val requestWep = runRequest.getWorkflowEngineParams();
    val msgWep =
        EngineParams.newBuilder()
            .setLatest(requestWep.getLatest())
            .setDefaultContainer(requestWep.getDefaultContainer())
            .setLaunchDir(requestWep.getLaunchDir())
            .setRevision(requestWep.getRevision())
            .setProjectDir(requestWep.getProjectDir())
            .setWorkDir(requestWep.getWorkDir());

    if (requestWep.getResume() != null) {
      msgWep.setResume(requestWep.getResume().toString());
    }
    return WfMgmtRunMsg.newBuilder()
        .setRunId(runId)
        .setState(state)
        .setWorkflowUrl(runRequest.getWorkflowUrl())
        .setWorkflowParamsJsonStr(toJsonString(runRequest.getWorkflowParams()))
        .setWorkflowEngineParams(msgWep.build())
        .setTimestamp(Instant.now().toEpochMilli())
        .build();
  }

  private static WfMgmtRunMsg createWfMgmtRunMsg(String runId, RunState state) {
    return WfMgmtRunMsg.newBuilder()
        .setRunId(runId)
        .setTimestamp(Instant.now().toEpochMilli())
        .setState(state)
        .setWorkflowEngineParams(EngineParams.newBuilder().build())
        .build();
  }
}
