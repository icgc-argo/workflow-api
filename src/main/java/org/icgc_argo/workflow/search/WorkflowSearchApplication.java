package org.icgc_argo.workflow.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@SpringBootApplication
public class WorkflowSearchApplication {

  public static void main(String[] args) {
    SpringApplication.run(WorkflowSearchApplication.class, args);
  }
}
