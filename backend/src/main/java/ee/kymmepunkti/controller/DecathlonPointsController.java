package ee.kymmepunkti.controller;

import ee.kymmepunkti.dto.PointsCalculationRequest;
import ee.kymmepunkti.dto.PointsCalculationResponse;
import ee.kymmepunkti.service.DecathlonPointsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/decathlon")
public class DecathlonPointsController {

    private final DecathlonPointsService pointsService;

    public DecathlonPointsController(DecathlonPointsService pointsService) {
        this.pointsService = pointsService;
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
