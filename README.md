# workflow-api
This service exposes a WES compliant REST API and an ARGO Graphql API for getting, starting and canceling runs. Data fetching for both APIs is backed by elasticsearch (filter, paging, & sorting).

## Tech Stack
- Java 11
- SpringBoot
- Spring Security
- Springfox Swagger
- Elasticsearch
- Graphql-java
- Apollo Federation
- Reactor Rabbitmq Streams

## REST API Endpoints

* List the workflow runs:
 
    `GET /runs`

* Get detailed info about a workflow run:
 
    `GET /runs/{run_id}`

* Get quick status info about a workflow run:
    
    `GET /runs/{run_id}/status`

* Get information about workflow execution service:
    
    `GET /service-info`
    
* Send request to start new runs:
    
    `POST /runs`

* Post request to cancel existing run:
    
    `POST /runs/{run_id}/cancel`


If using `secure` profile, include token in authorization header: `{ Authorization: Bearer $JWT }`

## Graphql

* Graphql Endpoint:

    `POST /graphql`

* full schema can be found here:  `./src/main/resources/schema.graphql`

If using `secure` profile, include token in authorization header: `{ Authorization: Bearer $JWT }`

#### Apollo Federation support:
This service has support for Apollo Federation which extends certain gql types found in [song-search](https://github.com/icgc-argo/song-search).
 
With a service like [rdpc-gateway](https://github.com/icgc-argo/rdpc-gateway) the schemas from these two services can be federated into a larger schema that joins the entities.  

## Configuration

Configuration is setup in `./src/main/resources/application.yaml`

#### Elasticsearch:
This service requires two elastic search index, `workflowIndex` and `taskIndex`. 

The mappings for these indices can be found from `workflow-relay`'s mappings: 

workflow - https://github.com/icgc-argo/workflow-relay/blob/develop/src/main/resources/run_log_mapping.json

task - https://github.com/icgc-argo/workflow-relay/blob/develop/src/main/resources/task_log_mapping.json

Configure other es properties as required.

#### Secure profile:
 The `secure` profile enables Oauth2 scope based authorization on requests. 
 
 Configure `jwtPublicKeyUrl` (or `jwtPublicKeyStr` for dev setup) in conjunction with the JWT issuer. Also configure the expected scopes as needed.

## Test

```bash
mvn clean test
```

## Build
With maven:
```bash
mvn clean package
```
With docker:
```bash 
docker build . -t icgcargo/workflow-api:test
```

## Run
Maven with app default and secure profile:
```bash
mvn spring-boot:run
```
```bash
mvn -Dspring-boot.run.profiles=secure spring-boot:run
```

Docker with app default and secure profile:
```bash
docker run icgcargo/workflow-api:test
```
```bash
docker run -e "SPRING_PROFILES_ACTIVE=secure" icgcargo/workflow-api:test
```
