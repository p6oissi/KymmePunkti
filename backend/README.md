# Decathlon Points API

A Spring Boot API that calculates individual event points and full totals for the men's decathlon.

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

## Calculate a full decathlon

`POST /api/v1/decathlon/total`

The request must contain all ten events exactly once. Results use the same seconds and metres units as the single-event endpoint.

```json
{
  "results": [
    { "event": "HUNDRED_METRES", "result": 10.4 },
    { "event": "LONG_JUMP", "result": 7.76 },
    { "event": "SHOT_PUT", "result": 18.4 },
    { "event": "HIGH_JUMP", "result": 2.21 },
    { "event": "FOUR_HUNDRED_METRES", "result": 46.17 },
    { "event": "HUNDRED_TEN_METRES_HURDLES", "result": 13.8 },
    { "event": "DISCUS_THROW", "result": 56.17 },
    { "event": "POLE_VAULT", "result": 5.29 },
    { "event": "JAVELIN_THROW", "result": 77.19 },
    { "event": "FIFTEEN_HUNDRED_METRES", "result": 233.79 }
  ]
}
```

The response contains the grand total and an event-by-event points breakdown in official competition order.

```json
{
  "totalPoints": 10002,
  "results": [
    {
      "event": "HUNDRED_METRES",
      "displayName": "100 Metres",
      "result": 10.4,
      "unit": "SECONDS",
      "points": 999
    }
  ]
}
```

The shortened response example shows one breakdown entry; the real response always contains all ten.

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

The current scope intentionally excludes persistence, authentication, and athlete management.
