package ee.kymmepunkti.dto;

import ee.kymmepunkti.domain.DecathlonEvent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PointsCalculationRequest(
        @NotNull(message = "Event is required")
        DecathlonEvent event,

        @NotNull(message = "Result is required")
        @Positive(message = "Result must be greater than zero")
        Double result
) { }
