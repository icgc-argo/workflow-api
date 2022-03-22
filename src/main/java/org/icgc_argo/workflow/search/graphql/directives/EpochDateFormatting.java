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

package org.icgc_argo.workflow.search.graphql.directives;

import static graphql.schema.DataFetcherFactories.wrapDataFetcher;

import graphql.Scalars;
import graphql.schema.FieldCoordinates;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

/**
 * This directive is Modified version of the dateFormat from graphql-java:
 * https://www.graphql-java.com/documentation/sdl-directives#another-example---date-formatting.
 *
 * <p>It is used to add format functionalities for epoch time fields.
 */
@Slf4j
public class EpochDateFormatting implements SchemaDirectiveWiring {
  private static final String ARGUMENT_NAME = "format";
  private static final String ARGUMENT_DESCRIPTION =
      "See Java SimpleDateFormat for patterns rules and examples: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html";

  @Override
  public GraphQLFieldDefinition onField(
      SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
    val field = environment.getElement();
    val parentType = environment.getFieldsContainer();

    val originalFetcher = environment.getCodeRegistry().getDataFetcher(parentType, field);
    val dataFetcher =
        wrapDataFetcher(
            originalFetcher,
            ((env, value) -> {
              val format = env.getArgument(ARGUMENT_NAME);
              if (isEmpty(format) || isEmpty(value)) {
                return value;
              }
              try {
                val dateTimeFormatter = buildFormatter(format.toString());
                val epochTime = Long.parseLong(value.toString());
                val date = new Date(epochTime);
                return dateTimeFormatter.format(date);
              } catch (Exception e) {
                log.warn(e.getLocalizedMessage());
                return value;
              }
            }));

    val coordinates = FieldCoordinates.coordinates(parentType, field);
    environment.getCodeRegistry().dataFetcher(coordinates, dataFetcher);

    return field.transform(
        builder ->
            builder.argument(
                GraphQLArgument.newArgument()
                    .name(ARGUMENT_NAME)
                    .description(ARGUMENT_DESCRIPTION)
                    .type(Scalars.GraphQLString)));
  }

  private static Boolean isEmpty(Object obj) {
    return obj == null || obj.toString().isEmpty();
  }

  private static SimpleDateFormat buildFormatter(@NonNull String format) {
    return new SimpleDateFormat(format);
  }
}
