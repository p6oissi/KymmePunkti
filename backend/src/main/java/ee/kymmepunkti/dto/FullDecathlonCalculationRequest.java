package ee.kymmepunkti.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record FullDecathlonCalculationRequest(
        @NotNull(message = "Results are required")
        @Size(min = 10, max = 10, message = "Exactly ten event results are required")
        List<@Valid PointsCalculationRequest> results
) { }
