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

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import java.util.concurrent.CompletableFuture;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import reactor.core.publisher.Mono;

/**
 * An extension of the DataFetcher functional interface used to describe lambda taking
 * DataFetchingEnviornment and returning a Mono (applyMonoDataFetcher). The DataFetcher's get
 * function is defaulted to a function which executes the applyMonoDataFetcher and adds any gql
 * context into the subscriber context.
 *
 * @param <T>
 */
@FunctionalInterface
public interface MonoDataFetcher<T> extends DataFetcher {
  Mono<T> applyMonoDataFetcher(DataFetchingEnvironment env);

  default CompletableFuture<T> get(DataFetchingEnvironment environment) {
    // add reactive security context to mono subscriberContext so subscribers (i.e. method
    // security) have access
    if (environment.getContext() instanceof SecurityContext) {
      SecurityContext context = environment.getContext();
      return applyMonoDataFetcher(environment)
          .subscriberContext(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)))
          .toFuture();
    }
    return applyMonoDataFetcher(environment).toFuture();
  }
}
