package ee.kymmepunkti.service;

import ee.kymmepunkti.domain.DecathlonEvent;
import ee.kymmepunkti.domain.DecathlonPerformance;
import ee.kymmepunkti.domain.FullDecathlonScore;

import java.util.List;

public interface DecathlonPointsService {

    int calculatePoints(DecathlonEvent event, double result);

    FullDecathlonScore calculateFullDecathlon(List<DecathlonPerformance> performances);

    List<DecathlonEvent> getEvents();
}
