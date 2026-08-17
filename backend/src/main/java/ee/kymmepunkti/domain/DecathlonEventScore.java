package ee.kymmepunkti.domain;

public record DecathlonEventScore(
        DecathlonEvent event,
        double result,
        int points
) { }
