package ee.kymmepunkti.dto;

import ee.kymmepunkti.domain.DecathlonEvent;
import ee.kymmepunkti.domain.MeasurementUnit;

public record DecathlonEventResponse(
        DecathlonEvent event,
        String displayName,
        MeasurementUnit unit
) { }
