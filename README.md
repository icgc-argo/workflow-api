# workflow-search
Search API for getting workflow run information.

## REST API Endpoints

* List the workflow runs:
 
    `GET /runs`

* Get detailed info about a workflow run:
 
    `GET /runs/{run_id}`

* Get quick status info about a workflow run:
    
    `GET /runs/{run_id}/status`

* Get information about workflow execution service:
    
    `GET /service-info`

If using `secure` profile include Authorization header with token: `{ Authorization: Bearer $JWT }`

## Graphql

* Graphql Endpoint:
 
    `POST /graphql`

* With `secure` enabled

    Include Authorization header with token: `{ Authorization: Bearer $JWT }`

* full schema can be found here: `./src/main/resources/schema.graphql`

## Configuration

Configuration is setup in `./src/main/resources/application.yaml`

#### Elasticsearch
This service requires two elastic search index, `workflowIndex` and `taskIndex`. 

The mappings for these indices can be found from Maestro's mapping: 

workflow - https://github.com/icgc-argo/workflow-relay/blob/develop/src/main/resources/run_log_mapping.json

task - https://github.com/icgc-argo/workflow-relay/blob/develop/src/main/resources/task_log_mapping.json

Configure other es properties as required.

#### Secure profile
 There is a `secure` profile that enables Oauth2 scope based authorization on requests. 
 
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
docker build . -t icgcargo/workflow-search:test
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
docker run icgcargo/song-search:test
```
```bash
docker run -e "SPRING_PROFILES_ACTIVE=secure" icgcargo/workflow-search:test
```
