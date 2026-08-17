package ee.kymmepunkti.domain;

import java.util.List;

public record FullDecathlonScore(
        int totalPoints,
        List<DecathlonEventScore> eventScores
) {
    public FullDecathlonScore {
        eventScores = List.copyOf(eventScores);
    }
}
