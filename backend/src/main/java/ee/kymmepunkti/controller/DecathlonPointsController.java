package ee.kymmepunkti.controller;

import ee.kymmepunkti.domain.DecathlonPerformance;
import ee.kymmepunkti.dto.DecathlonEventResponse;
import ee.kymmepunkti.dto.DecathlonEventScoreResponse;
import ee.kymmepunkti.dto.FullDecathlonCalculationRequest;
import ee.kymmepunkti.dto.FullDecathlonCalculationResponse;
import ee.kymmepunkti.dto.PointsCalculationRequest;
import ee.kymmepunkti.dto.PointsCalculationResponse;
import ee.kymmepunkti.service.DecathlonPointsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/decathlon")
public class DecathlonPointsController {

    private final DecathlonPointsService pointsService;

    public DecathlonPointsController(DecathlonPointsService pointsService) {
        this.pointsService = pointsService;
    }

    @GetMapping("/events")
    public ResponseEntity<List<DecathlonEventResponse>> getEvents() {
        var response = new ArrayList<DecathlonEventResponse>();

        for (var event : pointsService.getEvents()) {
            response.add(new DecathlonEventResponse(
                    event,
                    event.displayName(),
                    event.measurementUnit()
            ));
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/points")
    public ResponseEntity<PointsCalculationResponse> calculatePoints(
            @Valid @RequestBody PointsCalculationRequest request
    ) {
        int points = pointsService.calculatePoints(request.event(), request.result());
        var response = new PointsCalculationResponse(
                request.event(),
                request.result(),
                request.event().measurementUnit(),
                points
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/total")
    public ResponseEntity<FullDecathlonCalculationResponse> calculateTotal(
            @Valid @RequestBody FullDecathlonCalculationRequest request
    ) {
        var performances = new ArrayList<DecathlonPerformance>();

        for (var result : request.results()) {
            performances.add(new DecathlonPerformance(result.event(), result.result()));
        }

        var fullScore = pointsService.calculateFullDecathlon(performances);
        var resultResponses = new ArrayList<DecathlonEventScoreResponse>();

        for (var eventScore : fullScore.eventScores()) {
            var event = eventScore.event();
            resultResponses.add(new DecathlonEventScoreResponse(
                    event,
                    event.displayName(),
                    eventScore.result(),
                    event.measurementUnit(),
                    eventScore.points()
            ));
        }

        return ResponseEntity.ok(new FullDecathlonCalculationResponse(
                fullScore.totalPoints(),
                resultResponses
        ));
    }
}
