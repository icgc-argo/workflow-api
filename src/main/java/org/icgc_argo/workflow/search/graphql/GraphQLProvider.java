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

package org.icgc_argo.workflow.search.graphql;

import static graphql.Scalars.GraphQLLong;
import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import com.apollographql.federation.graphqljava.Federation;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import graphql.GraphQL;
import graphql.scalars.ExtendedScalars;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import java.io.IOException;
import java.net.URL;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow.search.graphql.directives.EpochDateFormatting;
import org.icgc_argo.workflow.search.graphql.fetchers.*;
import org.icgc_argo.workflow.search.model.common.Run;
import org.icgc_argo.workflow.search.model.graphql.Analysis;
import org.icgc_argo.workflow.search.model.graphql.Workflow;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GraphQLProvider {
  /** State */
  private GraphQL graphQL;

  /** Dependencies */
  private final RunDataFetchers runDataFetchers;

  private final TaskDataFetchers taskDataFetchers;
  private final EntityDataFetchers entityDataFetchers;
  private final MutationDataFetcher mutationDataFetcher;
  private final WorkflowDataFetcher workflowDataFetcher;


  @PostConstruct
  public void init() throws IOException {
    URL url = Resources.getResource("schema.graphqls");
    String sdl = Resources.toString(url, Charsets.UTF_8);
    val graphQLSchema = buildSchema(sdl);
    this.graphQL = GraphQL.newGraphQL(graphQLSchema).build();
  }

  @Bean
  public GraphQL graphQL() {
    return graphQL;
  }

  private GraphQLSchema buildSchema(String sdl) {
    return Federation.transform(sdl, buildWiring())
        .fetchEntities(entityDataFetchers.getDataFetcher())
        .fetchEntities(workflowDataFetcher.getDataFetcher())
        .resolveEntityType(
            typeResolutionEnvironment -> {
              final Object src = typeResolutionEnvironment.getObject();
              if (src instanceof Run) {
                return typeResolutionEnvironment
                    .getSchema()
                    .getObjectType(EntityDataFetchers.RUN_ENTITY);
              }
              if (src instanceof Analysis) {
                return typeResolutionEnvironment
                    .getSchema()
                    .getObjectType(EntityDataFetchers.ANALYSIS_ENTITY);
              }
              if (src instanceof Workflow) {
                return typeResolutionEnvironment
                    .getSchema()
                    .getObjectType(EntityDataFetchers.WORKFLOW_ENTITY);
              }
              return null;
            })
        .build();
  }

  private RuntimeWiring buildWiring() {
    return RuntimeWiring.newRuntimeWiring()
        .directive("epochDateFormat", new EpochDateFormatting())
        .scalar(GraphQLLong)
        .scalar(ExtendedScalars.Json)
        .type(newTypeWiring("Mutation").dataFetchers(mutationDataFetcher.get()))
        .type(newTypeWiring("Query").dataFetcher("runs", runDataFetchers.getRunsDataFetcher()))
        .type(
            newTypeWiring("Query")
                .dataFetcher("aggregateRuns", runDataFetchers.getAggregateRunsDataFetcher()))
        .type(newTypeWiring("Query").dataFetcher("tasks", taskDataFetchers.getTasksDataFetcher()))
        .type(
            newTypeWiring("Query")
                .dataFetcher("aggregateTasks", taskDataFetchers.getAggregateTasksDataFetcher()))
        .type(
            newTypeWiring("Run").dataFetcher("tasks", taskDataFetchers.getNestedTaskDataFetcher()))
        .type(
            newTypeWiring("Task")
                .dataFetcher("run", runDataFetchers.getNestedRunInTaskDataFetcher()))
        .type(
            newTypeWiring("Analysis")
                .dataFetcher("inputForRuns", runDataFetchers.getNestedRunInAnalysisDataFetcher()))
        .build();
  }
}
