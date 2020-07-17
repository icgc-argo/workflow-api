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

package org.icgc_argo.workflow.search.graphql.security;

import com.google.common.collect.ImmutableList;
import graphql.ExecutionResult;
import graphql.ExecutionResultImpl;
import graphql.GraphqlErrorBuilder;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategy;
import graphql.execution.ExecutionStrategyParameters;
import graphql.language.Field;
import graphql.language.Node;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
public class VerifyAuthQueryExecutionDecorator extends ExecutionStrategy {

    private final List<String> acceptedQueryAuthScopes;
    private final ExecutionStrategy decoratedStrategy;

    public VerifyAuthQueryExecutionDecorator(ExecutionStrategy decoratedStrategy, List<String> acceptedQueryAuthScopes) {
        this.decoratedStrategy = decoratedStrategy;
        this.acceptedQueryAuthScopes = acceptedQueryAuthScopes;
    }

    @Override
    @SneakyThrows
    public CompletableFuture<ExecutionResult> execute(ExecutionContext executionContext, ExecutionStrategyParameters executionStrategyParameters) {
        if (isApolloFederationServiceCapabilityQuery(executionContext)) {
            // the apollo federation requests are queries but they don't need to be authorized so no need to check
            return decoratedStrategy.execute(executionContext, executionStrategyParameters);
        }

        val grantedAuthorities = getGrantedAuthoritiesFromContext(executionContext.getContext());

        val foundAcceptedAuthScopes = getAcceptedAuthScopesFromGrantedAuthorities(grantedAuthorities);

        log.debug("Context auth scopes found in decorator: " + foundAcceptedAuthScopes.toString());

        if (foundAcceptedAuthScopes.size() <= 0) {
            return permissionDeniedResult();
        }

        return decoratedStrategy.execute(executionContext, executionStrategyParameters);
    }

    private ImmutableList<GrantedAuthority> getGrantedAuthoritiesFromContext(Object context) {
        val scopesBuilder = ImmutableList.<GrantedAuthority>builder();
        if (context instanceof SecurityContext) {
            try {
                SecurityContext securityContext = (SecurityContext) context;
                scopesBuilder.addAll(securityContext.getAuthentication().getAuthorities());
            } catch (Exception e) {
                log.error("Failed to extract security authorities from context in graphql execution input");
            }
        }
        return scopesBuilder.build();
    }

    private List<String> getAcceptedAuthScopesFromGrantedAuthorities(ImmutableList<GrantedAuthority> grantedAuthorities) {
        return grantedAuthorities.stream()
                    .map(GrantedAuthority::getAuthority)
                    .filter(acceptedQueryAuthScopes::contains)
                    .collect(Collectors.toUnmodifiableList());
    }

    private CompletableFuture<ExecutionResult> permissionDeniedResult() {
        val deniedError = GraphqlErrorBuilder.newError()
                                  .message("Permission Denied")
                                  .build();
        ExecutionResult result = new ExecutionResultImpl(deniedError);
        return CompletableFuture.completedFuture(result);
    }

    // As per the apollo federation spec (https://www.apollographql.com/docs/apollo-server/federation/federation-spec/#fetch-service-capabilities),
    // each service in a federation needs to provide service capability information represented as `SDL` via the `_service` query.
    // This function will check if operation is from Apollo and if the query in the executionContext matches:
    //    query {
    //          _Service {
    //              sdl
    //          }
    //     }
    private boolean isApolloFederationServiceCapabilityQuery(ExecutionContext executionContext) {
        final String federationExpectedOperationName = "__ApolloGetServiceDefinition__";
        final String federationExpectedFirstLevelQueryField = "_service";
        final String federationExpectedSecondLevelQuery = "sdl";

        val operationName = executionContext.getOperationDefinition().getName();
        if (operationName == null || !operationName.equalsIgnoreCase(federationExpectedOperationName)) {
            return false;
        }

        val firstLevelQueryFields = executionContext.getOperationDefinition().getSelectionSet().getChildren();
        val firstLevelQueryField = getExpectedAloneField(firstLevelQueryFields, federationExpectedFirstLevelQueryField);
        if (firstLevelQueryField == null) return false;

        val secondLevelQueryFields = firstLevelQueryField.getSelectionSet().getChildren();
        val secondLevelQueryField = getExpectedAloneField(secondLevelQueryFields, federationExpectedSecondLevelQuery);
        if (secondLevelQueryField == null) return false;

        return true;
    }

    private Field getExpectedAloneField(List<Node> fields, String onlyFieldExpectedToExist) {
        if (fields.size() > 1) return null;

        Field singleField;
        try {
            val singleFieldOpt = fields.stream().findFirst();
            if (singleFieldOpt.isEmpty()) return null;
            singleField = (Field) singleFieldOpt.get();
        } catch (Exception e) {
            log.error("Failed to find just one field in list of fields");
            return null;
        }

        return singleField.getName().equalsIgnoreCase(onlyFieldExpectedToExist) ?
                       singleField : null;
    }
}
