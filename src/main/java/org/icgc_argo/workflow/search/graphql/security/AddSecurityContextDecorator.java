package org.icgc_argo.workflow.search.graphql.security;

import graphql.ExecutionResult;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionStrategy;
import graphql.execution.ExecutionStrategyParameters;
import graphql.execution.NonNullableFieldWasNullException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class AddSecurityContextDecorator extends ExecutionStrategy {

    private final ExecutionStrategy decoratedStrategy;

    public AddSecurityContextDecorator(ExecutionStrategy decoratedStrategy) {
        this.decoratedStrategy = decoratedStrategy;
    }

    @Override
    public CompletableFuture<ExecutionResult> execute(ExecutionContext executionContext, ExecutionStrategyParameters executionStrategyParameters) throws NonNullableFieldWasNullException {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        ExecutionContext modifiedContext = executionContext.transform(execContext -> execContext.context(securityContext));
        return decoratedStrategy.execute(modifiedContext, executionStrategyParameters);
    }
}
