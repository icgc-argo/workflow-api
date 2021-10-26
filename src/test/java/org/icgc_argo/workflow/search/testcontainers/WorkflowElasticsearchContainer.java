package org.icgc_argo.workflow.search.testcontainers;

import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class WorkflowElasticsearchContainer extends ElasticsearchContainer {

  public static final String ELASTIC_SEARCH_DOCKER =
      "docker.elastic.co/elasticsearch/elasticsearch:7.6.0";

  public static final String CLUSTER_NAME = "cluster.name";

  public static final String ELASTIC_SEARCH = "elasticsearch";

  public WorkflowElasticsearchContainer() {
    super(ELASTIC_SEARCH_DOCKER);
    this.addFixedExposedPort(9200, 9200);
    this.addFixedExposedPort(9300, 9300);
    this.addEnv(CLUSTER_NAME, ELASTIC_SEARCH);
  }
}
