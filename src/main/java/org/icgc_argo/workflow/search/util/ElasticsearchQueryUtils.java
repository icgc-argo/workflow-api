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

package org.icgc_argo.workflow.search.util;

import static java.util.stream.Collectors.toUnmodifiableList;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.val;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.icgc_argo.workflow.search.model.graphql.Range;
import org.icgc_argo.workflow.search.model.graphql.Sort;

public class ElasticsearchQueryUtils {
  /**
   * For each argument, find its query producer function and apply the argument value ANDing it in a
   * bool query
   *
   * @param args Argument Map from GraphQL
   * @return Elasticsearch Bool Query containing ANDed (MUSTed) term queries
   */
  public static BoolQueryBuilder queryFromArgs(
      Map<String, Function<String, AbstractQueryBuilder<?>>> QUERY_RESOLVER,
      Map<String, Object> args) {
    val bool = QueryBuilders.boolQuery();
    args.forEach(
        (key, value) ->
            bool.must(
                QUERY_RESOLVER
                    .getOrDefault(key, simpleTermQueryBuilderResolver(key))
                    .apply(value.toString())));
    return bool;
  }

  /**
   * For each sorts, find its SortBuilder and add the sort order value then collect them all in a
   * list
   *
   * @param sorts List of Sort objects
   * @return List of FiledSortBuilder
   */
  public static List<FieldSortBuilder> sortsToEsSortBuilders(
      Map<String, FieldSortBuilder> SORT_BUILDER_RESOLVER, List<Sort> sorts) {
    return sorts.stream()
        .map(
            sort -> {
              val sortBuilder = SORT_BUILDER_RESOLVER.get(sort.getFieldName());
              return sortBuilder.order(SortOrder.fromString(sort.getOrder()));
            })
        .collect(toUnmodifiableList());
  }

  public static List<RangeQueryBuilder> rangesToEsRangeQueryBuilders(
          Map<String, RangeQueryBuilder> RANGE_BUILDER_RESOLVER, List<Range> ranges) {
    return ranges.stream()
            .map(
                    range -> {
                      val rangeQueryBuilderBuilder = RANGE_BUILDER_RESOLVER.get(range.getFieldName());
                      if (range.getFrom() != null) {
                          rangeQueryBuilderBuilder
                                  .from(range.getFrom().getValue(), range.getFrom().getInclusive());
                      }
                      if (range.getTo() != null) {
                          rangeQueryBuilderBuilder.to(range.getTo().getValue(), range.getTo().getInclusive());
                      }
                      return rangeQueryBuilderBuilder;
                    })
            .collect(toUnmodifiableList());
  }


  private static Function<String, AbstractQueryBuilder<?>> simpleTermQueryBuilderResolver(
      String key) {
    return v -> new TermQueryBuilder(key, v);
  }
}
