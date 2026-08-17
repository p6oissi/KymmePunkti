package ee.kymmepunkti.controller;

import ee.kymmepunkti.dto.DecathlonEventResponse;
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
}
