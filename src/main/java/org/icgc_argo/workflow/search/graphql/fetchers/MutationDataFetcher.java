/*
 * Copyright (c) 2021 The Ontario Institute for Cancer Research. All rights reserved
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

package org.icgc_argo.workflow.search.graphql.fetchers;

import static org.icgc_argo.workflow.search.util.JacksonUtils.convertValue;

import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetcher;
import java.util.Map;
import lombok.NonNull;
import lombok.val;
import org.icgc_argo.workflow.search.graphql.AsyncDataFetcher;
import org.icgc_argo.workflow.search.model.common.RunRequest;
import org.icgc_argo.workflow.search.model.graphql.GqlRunRequest;
import org.icgc_argo.workflow.search.model.wes.RunId;
import org.icgc_argo.workflow.search.service.graphql.Messenger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MutationDataFetcher {

  @NonNull private final AsyncDataFetcher<RunId> cancelRunResolver;
  @NonNull private final AsyncDataFetcher<RunId> startRunResolver;

  @Autowired
  public MutationDataFetcher(Messenger messenger) {
    cancelRunResolver =
        env -> {
          val args = env.getArguments();
          String runId = String.valueOf(args.get("runId"));
          return messenger.cancel(runId);
        };

    startRunResolver =
        env -> {
          val args = env.getArguments();

          val requestMap = ImmutableMap.<String, Object>builder();

          if (args.get("request") != null)
            requestMap.putAll((Map<String, Object>) args.get("request"));

          RunRequest runRequest = convertValue(requestMap.build(), GqlRunRequest.class);
          return messenger.run(runRequest);
        };
  }

  public Map<String, DataFetcher> get() {
    return Map.of(
        "cancelRun", cancelRunResolver,
        "startRun", startRunResolver);
  }
}
