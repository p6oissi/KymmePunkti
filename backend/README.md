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

## Scoring rules

The formulas and coefficients come from the official [World Athletics Scoring Tables for Combined Events](https://worldathletics.org/download/download?filename=c651eeb3-0f9d-47c0-9314-a3bd001e0960.pdf&urlslug=IAAF+Scoring+Tables+for+Combined+Events), published on the [World Athletics technical information page](https://worldathletics.org/about-iaaf/documents/technical-information).

```text
Track:  points = floor(A × (B − timeInSeconds)^C)
Jumps:  points = floor(A × (distanceInCentimetres − B)^C)
Throws: points = floor(A × (distanceInMetres − B)^C)
```

The API accepts jump results in metres and converts them to centimetres before applying the formula. The 1500 metres result must be supplied as total seconds; for example, `3:53.79` is `233.79` seconds.

The API assumes automatic timing. World Athletics applies adjustments to manual times before scoring: `+0.24` seconds for the 100 metres and 110 metres hurdles, and `+0.14` seconds for the 400 metres. Manual timing is not supported by this API.

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

The current scope intentionally excludes persistence, authentication, athlete management, and combined totals.
