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

package org.icgc_argo.workflow.search.config.elasticsearch;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.reactive.ReactiveElasticsearchClient;
import org.springframework.data.elasticsearch.client.reactive.ReactiveRestClients;
import org.springframework.web.reactive.function.client.ExchangeStrategies;

@Configuration
@Slf4j
public class ElasticsearchConfig {

  private ElasticsearchProperties properties;

  private static final Integer connectTimeout = 15_000;
  private static final Integer connectionRequestTimeout = 15_000;
  private static final Integer socketTimeout = 15_000;

  @Autowired
  public ElasticsearchConfig(@NonNull ElasticsearchProperties properties) {
    this.properties = properties;
    log.info(
        String.format(
            "Connecting to Elasticsearch host %s, port %s.",
            properties.getHost(), properties.getPort()));
  }

  @Bean
  public ReactiveElasticsearchClient reactiveEsClient() {
    val hostAndPort = properties.getHost() + ":" + properties.getPort();
    val configurationBuilder = ClientConfiguration.builder().connectedTo(hostAndPort);

    if (properties.getUseHttps()) {
      configurationBuilder.usingSsl();
    }

    if (properties.getUseAuthentication()) {
      configurationBuilder.withBasicAuth(properties.getUsername(), properties.getPassword());
    }

    configurationBuilder.withConnectTimeout(connectTimeout);
    configurationBuilder.withSocketTimeout(socketTimeout);

    // Avoid default buffer size limit
    configurationBuilder.withWebClientConfigurer(
        webClient -> {
          val exchangeStrategies =
              ExchangeStrategies.builder()
                  .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
                  .build();
          return webClient.mutate().exchangeStrategies(exchangeStrategies).build();
        });

    return ReactiveRestClients.create(configurationBuilder.build());
  }
}
