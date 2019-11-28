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

## Build

With maven:
```bash
mvn clean package
```

With docker:
```bash 
docker build .
```

## Run

```bash
java -jar workflow-search-0.0.1-SNAPSHOT.jar
```

With Docker:
```bash
docker run icgcargo/workflow-search
```

## Test

```bash
mvn clean test
```

