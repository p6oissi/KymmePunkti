package ee.kymmepunkti.dto;

import java.util.List;

public record FullDecathlonCalculationResponse(
        int totalPoints,
        List<DecathlonEventScoreResponse> results
) { }
