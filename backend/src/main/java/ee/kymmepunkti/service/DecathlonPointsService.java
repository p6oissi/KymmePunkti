package ee.kymmepunkti.service;

import ee.kymmepunkti.domain.DecathlonEvent;

import java.util.List;

public interface DecathlonPointsService {

    int calculatePoints(DecathlonEvent event, double result);

    List<DecathlonEvent> getEvents();
}
