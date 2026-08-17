package ee.kymmepunkti.service;

import ee.kymmepunkti.domain.DecathlonEvent;
import ee.kymmepunkti.domain.DecathlonEventScore;
import ee.kymmepunkti.domain.DecathlonPerformance;
import ee.kymmepunkti.domain.CalculationType;
import ee.kymmepunkti.domain.FullDecathlonScore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

@Service
public class WorldAthleticsDecathlonPointsService implements DecathlonPointsService {

    private static final double METRES_TO_CENTIMETRES = 100.0;

    @Override
    public int calculatePoints(DecathlonEvent event, double result) {
        validate(event, result);

        if (event.calculationType() == CalculationType.TRACK) {
            return calculateTrackPoints(event, result);
        }

        if (event.calculationType() == CalculationType.JUMP) {
            return calculateJumpPoints(event, result);
        }

        return calculateThrowPoints(event, result);
    }

    @Override
    public FullDecathlonScore calculateFullDecathlon(List<DecathlonPerformance> performances) {
        if (performances == null || performances.size() != DecathlonEvent.values().length) {
            throw new IllegalArgumentException("Exactly ten event results are required");
        }

        var resultsByEvent = new EnumMap<DecathlonEvent, Double>(DecathlonEvent.class);

        for (var performance : performances) {
            if (performance == null) {
                throw new IllegalArgumentException("Event result must not be null");
            }

            validate(performance.event(), performance.result());

            if (resultsByEvent.put(performance.event(), performance.result()) != null) {
                throw new IllegalArgumentException("Each decathlon event must be provided exactly once");
            }
        }

        var eventScores = new ArrayList<DecathlonEventScore>();
        int totalPoints = 0;

        for (var event : getEvents()) {
            Double result = resultsByEvent.get(event);
            if (result == null) {
                throw new IllegalArgumentException("Each decathlon event must be provided exactly once");
            }

            int points = calculatePoints(event, result);
            eventScores.add(new DecathlonEventScore(event, result, points));
            totalPoints += points;
        }

        return new FullDecathlonScore(totalPoints, eventScores);
    }

    @Override
    public List<DecathlonEvent> getEvents() {
        return List.of(DecathlonEvent.values());
    }

    private int calculateTrackPoints(DecathlonEvent event, double resultInSeconds) {
        double formulaBase = event.coefficientB() - resultInSeconds;
        return applyFormula(event, formulaBase);
    }

    private int calculateJumpPoints(DecathlonEvent event, double resultInMetres) {
        double resultInCentimetres = resultInMetres * METRES_TO_CENTIMETRES;
        double formulaBase = resultInCentimetres - event.coefficientB();
        return applyFormula(event, formulaBase);
    }

    private int calculateThrowPoints(DecathlonEvent event, double resultInMetres) {
        double formulaBase = resultInMetres - event.coefficientB();
        return applyFormula(event, formulaBase);
    }

    private int applyFormula(DecathlonEvent event, double formulaBase) {
        if (formulaBase <= 0) {
            return 0;
        }

        return (int) Math.floor(
                event.coefficientA() * Math.pow(formulaBase, event.coefficientC())
        );
    }

    private void validate(DecathlonEvent event, double result) {
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null");
        }
        if (!Double.isFinite(result) || result <= 0) {
            throw new IllegalArgumentException("Result must be a finite number greater than zero");
        }
    }
}
