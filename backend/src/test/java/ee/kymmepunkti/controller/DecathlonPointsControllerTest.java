package ee.kymmepunkti.controller;

import ee.kymmepunkti.domain.DecathlonEvent;
import ee.kymmepunkti.service.DecathlonPointsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
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
}
