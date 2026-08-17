package ee.kymmepunkti.service;

import ee.kymmepunkti.domain.DecathlonEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WorldAthleticsDecathlonPointsServiceTest {

    private final DecathlonPointsService service = new WorldAthleticsDecathlonPointsService();

    @ParameterizedTest
    @MethodSource("officialScoringExamples")
    void calculatesPointsUsingWorldAthleticsFormula(
            DecathlonEvent event,
            double result,
            int expectedPoints
    ) {
        assertEquals(expectedPoints, service.calculatePoints(event, result));
    }

    @ParameterizedTest
    @MethodSource("zeroPointPerformances")
    void returnsZeroOutsidePositiveScoringRange(DecathlonEvent event, double result) {
        assertEquals(0, service.calculatePoints(event, result));
    }

    @Test
    void roundsPointsDown() {
        assertEquals(999, service.calculatePoints(DecathlonEvent.HUNDRED_METRES, 10.40));
    }

    @Test
    void rejectsNullEvent() {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.calculatePoints(null, 10.40)
        );

        assertEquals("Event must not be null", exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY})
    void rejectsInvalidResult(double result) {
        var exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.calculatePoints(DecathlonEvent.HUNDRED_METRES, result)
        );

        assertEquals("Result must be a finite number greater than zero", exception.getMessage());
    }

    private static Stream<Arguments> officialScoringExamples() {
        return Stream.of(
                Arguments.of(DecathlonEvent.HUNDRED_METRES, 10.39, 1001),
                Arguments.of(DecathlonEvent.LONG_JUMP, 7.76, 1000),
                Arguments.of(DecathlonEvent.SHOT_PUT, 18.40, 1000),
                Arguments.of(DecathlonEvent.HIGH_JUMP, 2.21, 1002),
                Arguments.of(DecathlonEvent.FOUR_HUNDRED_METRES, 46.17, 1000),
                Arguments.of(DecathlonEvent.HUNDRED_TEN_METRES_HURDLES, 13.80, 1000),
                Arguments.of(DecathlonEvent.DISCUS_THROW, 56.17, 1000),
                Arguments.of(DecathlonEvent.POLE_VAULT, 5.29, 1001),
                Arguments.of(DecathlonEvent.JAVELIN_THROW, 77.19, 1000),
                Arguments.of(DecathlonEvent.FIFTEEN_HUNDRED_METRES, 233.79, 1000)
        );
    }

    private static Stream<Arguments> zeroPointPerformances() {
        return Stream.of(
                Arguments.of(DecathlonEvent.HUNDRED_METRES, 18.0),
                Arguments.of(DecathlonEvent.LONG_JUMP, 2.20),
                Arguments.of(DecathlonEvent.SHOT_PUT, 1.50)
        );
    }
}
