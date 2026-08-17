package ee.kymmepunkti.dto;

import ee.kymmepunkti.domain.DecathlonEvent;
import ee.kymmepunkti.domain.MeasurementUnit;

public record DecathlonEventScoreResponse(
        DecathlonEvent event,
        String displayName,
        double result,
        MeasurementUnit unit,
        int points
) { }
