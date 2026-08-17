package ee.kymmepunkti.dto;

import ee.kymmepunkti.domain.DecathlonEvent;
import ee.kymmepunkti.domain.MeasurementUnit;

public record PointsCalculationResponse(
        DecathlonEvent event,
        double result,
        MeasurementUnit unit,
        int points
) { }
