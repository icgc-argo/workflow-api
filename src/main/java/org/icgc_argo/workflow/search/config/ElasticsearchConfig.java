package org.icgc_argo.workflow.search.config;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
  }

  private RestHighLevelClient secureRestHighLevelClient() {
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
    credentialsProvider.setCredentials(
        AuthScope.ANY,
        new UsernamePasswordCredentials(properties.getUsername(), properties.getPassword()));

    return new RestHighLevelClient(
        RestClient.builder(
                new HttpHost(
                    properties.getHost(),
                    properties.getPort(),
                    properties.getUseHttps() ? "https" : "http"))
            .setRequestConfigCallback(
                config ->
                    config
                        .setConnectTimeout(connectTimeout)
                        .setConnectionRequestTimeout(connectionRequestTimeout)
                        .setSocketTimeout(socketTimeout))
            .setHttpClientConfigCallback(
                new HttpClientConfigCallback() {
                  @Override
                  public HttpAsyncClientBuilder customizeHttpClient(
                      HttpAsyncClientBuilder httpClientBuilder) {
                    return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                  }
                }));
  }

  @Bean
  public RestHighLevelClient restHighLevelClient() {

    if (properties.getUseAuthentication()) {
      return secureRestHighLevelClient();
    }

    log.info(String.format("Connecting to Elasticsearch host %s, port %s.", properties.getHost(), properties.getPort()));

    return new RestHighLevelClient(
        RestClient.builder(
                new HttpHost(
                    properties.getHost(),
                    properties.getPort(),
                    properties.getUseHttps() ? "https" : "http"))
            .setRequestConfigCallback(
                config ->
                    config
                        .setConnectTimeout(connectTimeout)
                        .setConnectionRequestTimeout(connectionRequestTimeout)
                        .setSocketTimeout(socketTimeout)));
  }
}
