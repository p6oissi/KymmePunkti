# Decathlon Points API

A Spring Boot API that calculates points for one event in the men's decathlon.

## Requirements

- Java 25

Ensure `JAVA_HOME` points to a Java 25 JDK before using the Gradle wrapper.

## Run

```powershell
.\gradlew.bat bootRun
```

Swagger UI is available at <http://localhost:8080/swagger-ui.html>. The OpenAPI document is available at <http://localhost:8080/v3/api-docs>.

## Calculate points

`POST /api/v1/decathlon/points`

```json
{
  "event": "HUNDRED_METRES",
  "result": 10.4
}
```

```json
{
  "event": "HUNDRED_METRES",
  "result": 10.4,
  "unit": "SECONDS",
  "points": 999
}
```

Running times are supplied in seconds. Jump and throw distances are supplied in metres. A valid performance outside the positive scoring range receives zero points.

Supported events:

- `HUNDRED_METRES`
- `LONG_JUMP`
- `SHOT_PUT`
- `HIGH_JUMP`
- `FOUR_HUNDRED_METRES`
- `HUNDRED_TEN_METRES_HURDLES`
- `DISCUS_THROW`
- `POLE_VAULT`
- `JAVELIN_THROW`
- `FIFTEEN_HUNDRED_METRES`

## Test

```powershell
.\gradlew.bat clean test
```

The current scope intentionally excludes a frontend, Docker, persistence, authentication, athlete management, and combined totals.
