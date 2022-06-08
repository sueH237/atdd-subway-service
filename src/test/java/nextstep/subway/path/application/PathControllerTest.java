package nextstep.subway.path.application;

import com.google.common.collect.Lists;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;

import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PathControllerTest {
    @LocalServerPort
    int port;

    @MockBean
    PathService pathService;

    private StationResponse 강남역 = new StationResponse(1L,"강남역", LocalDateTime.now(),LocalDateTime.now());
    private StationResponse 양재역 = new StationResponse(2L,"양재역", LocalDateTime.now(),LocalDateTime.now());
    private StationResponse 교대역 = new StationResponse(3L,"교대역", LocalDateTime.now(),LocalDateTime.now());

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 최단경로요청시_정상응답여부_확인(){
        String startStationId = String.valueOf(강남역.getId());
        String endStationId = String.valueOf(교대역.getId());

        // Given
        when(pathService.findShortestPath(startStationId,endStationId))
                .thenReturn(new PathResponse(Lists.newArrayList(강남역,양재역,교대역)));
        // When
        ExtractableResponse<Response> response = pathController로_요청보내기(startStationId,endStationId);

        // Then
        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        PathResponse pathResponse = response.as(PathResponse.class);
        List<StationResponse> stations = pathResponse.getStations();
        List<String> stationNames = stations.stream().map((StationResponse::getName)).collect(toList());
        assertThat(stationNames)
                .hasSize(3)
                .containsExactly("강남역","양재역","교대역");
    }

    private ExtractableResponse<Response> pathController로_요청보내기(String startStationId, String endStationId) {
        Map<String,String> queryParams = new HashMap<>();
        queryParams.put("source",startStationId);
        queryParams.put("target",endStationId);

        ExtractableResponse<Response> response = RestAssured
                .given().log().all()
                .accept(ContentType.JSON)
                .queryParams(queryParams)
                .when().get("/paths")
                .then().log().all()
                .extract();
        return response;
    }
}
