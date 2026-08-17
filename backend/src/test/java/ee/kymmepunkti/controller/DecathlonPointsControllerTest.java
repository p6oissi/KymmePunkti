package ee.kymmepunkti.controller;

import ee.kymmepunkti.domain.DecathlonEvent;
import ee.kymmepunkti.domain.DecathlonEventScore;
import ee.kymmepunkti.domain.FullDecathlonScore;
import ee.kymmepunkti.service.DecathlonPointsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DecathlonPointsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DecathlonPointsService pointsService;

    @Test
    void returnsAllEventsInOfficialOrder() throws Exception {
        when(pointsService.getEvents()).thenReturn(List.of(DecathlonEvent.values()));

        mockMvc.perform(get("/api/v1/decathlon/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(10))
                .andExpect(jsonPath("$[0].event").value("HUNDRED_METRES"))
                .andExpect(jsonPath("$[0].displayName").value("100 Metres"))
                .andExpect(jsonPath("$[0].unit").value("SECONDS"))
                .andExpect(jsonPath("$[9].event").value("FIFTEEN_HUNDRED_METRES"))
                .andExpect(jsonPath("$[9].displayName").value("1500 Metres"));
    }

    @Test
    void calculatesPoints() throws Exception {
        when(pointsService.calculatePoints(DecathlonEvent.HUNDRED_METRES, 10.4)).thenReturn(999);

        mockMvc.perform(post("/api/v1/decathlon/points")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"event":"HUNDRED_METRES","result":10.4}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.event").value("HUNDRED_METRES"))
                .andExpect(jsonPath("$.result").value(10.4))
                .andExpect(jsonPath("$.unit").value("SECONDS"))
                .andExpect(jsonPath("$.points").value(999));
    }

    @Test
    void calculatesFullDecathlonTotalAndBreakdown() throws Exception {
        var eventScores = List.of(
                new DecathlonEventScore(DecathlonEvent.HUNDRED_METRES, 10.4, 999),
                new DecathlonEventScore(DecathlonEvent.LONG_JUMP, 7.76, 1000)
        );
        when(pointsService.calculateFullDecathlon(anyList()))
                .thenReturn(new FullDecathlonScore(1999, eventScores));

        mockMvc.perform(post("/api/v1/decathlon/total")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fullDecathlonRequest()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPoints").value(1999))
                .andExpect(jsonPath("$.results.length()").value(2))
                .andExpect(jsonPath("$.results[0].event").value("HUNDRED_METRES"))
                .andExpect(jsonPath("$.results[0].displayName").value("100 Metres"))
                .andExpect(jsonPath("$.results[0].result").value(10.4))
                .andExpect(jsonPath("$.results[0].unit").value("SECONDS"))
                .andExpect(jsonPath("$.results[0].points").value(999));
    }

    @Test
    void rejectsFullDecathlonWithFewerThanTenResults() throws Exception {
        mockMvc.perform(post("/api/v1/decathlon/total")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"results":[{"event":"HUNDRED_METRES","result":10.4}]}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Exactly ten event results are required"));
    }

    @Test
    void rejectsDuplicateEventInFullDecathlon() throws Exception {
        when(pointsService.calculateFullDecathlon(anyList()))
                .thenThrow(new IllegalArgumentException("Each decathlon event must be provided exactly once"));

        mockMvc.perform(post("/api/v1/decathlon/total")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fullDecathlonRequest().replace("FIFTEEN_HUNDRED_METRES", "HUNDRED_METRES")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Each decathlon event must be provided exactly once"));
    }

    @Test
    void rejectsInvalidResultInFullDecathlon() throws Exception {
        mockMvc.perform(post("/api/v1/decathlon/total")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fullDecathlonRequest().replace("10.4", "0")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Result must be greater than zero"));
    }

    @Test
    void rejectsUnknownEventInFullDecathlon() throws Exception {
        mockMvc.perform(post("/api/v1/decathlon/total")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(fullDecathlonRequest().replace("HUNDRED_METRES", "MARATHON")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unknown event: MARATHON"));
    }

    @Test
    void rejectsMalformedFullDecathlonJson() throws Exception {
        mockMvc.perform(post("/api/v1/decathlon/total")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed request body"));
    }

    @Test
    void rejectsMissingFields() throws Exception {
        mockMvc.perform(post("/api/v1/decathlon/points")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void rejectsNonPositiveResult() throws Exception {
        mockMvc.perform(post("/api/v1/decathlon/points")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"event":"HUNDRED_METRES","result":0}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Result must be greater than zero"));
    }

    @Test
    void rejectsUnknownEvent() throws Exception {
        mockMvc.perform(post("/api/v1/decathlon/points")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"event":"MARATHON","result":120}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unknown event: MARATHON"));
    }

    @Test
    void rejectsMalformedJson() throws Exception {
        mockMvc.perform(post("/api/v1/decathlon/points")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed request body"));
    }

    @Test
    void exposesOpenApiDocument() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Decathlon Points API"));
    }

    private static String fullDecathlonRequest() {
        return """
                {
                  "results": [
                    {"event":"HUNDRED_METRES","result":10.4},
                    {"event":"LONG_JUMP","result":7.76},
                    {"event":"SHOT_PUT","result":18.4},
                    {"event":"HIGH_JUMP","result":2.21},
                    {"event":"FOUR_HUNDRED_METRES","result":46.17},
                    {"event":"HUNDRED_TEN_METRES_HURDLES","result":13.8},
                    {"event":"DISCUS_THROW","result":56.17},
                    {"event":"POLE_VAULT","result":5.29},
                    {"event":"JAVELIN_THROW","result":77.19},
                    {"event":"FIFTEEN_HUNDRED_METRES","result":233.79}
                  ]
                }
                """;
    }
}
