package nextstep.subway.path.acceptance;

import static nextstep.subway.auth.acceptance.AuthAcceptanceTestActions.로그인_요청;
import static nextstep.subway.auth.application.AuthServiceTest.AGE;
import static nextstep.subway.auth.application.AuthServiceTest.EMAIL;
import static nextstep.subway.auth.application.AuthServiceTest.PASSWORD;
import static nextstep.subway.line.acceptance.LineAcceptanceTestActions.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.LineAcceptanceTestActions.지하철_노선에_지하철역_등록되어_있음;
import static nextstep.subway.member.MemberAcceptanceTestActions.회원_생성됨;
import static nextstep.subway.member.MemberAcceptanceTestActions.회원_생성을_요청;
import static nextstep.subway.path.acceptance.PathAcceptanceTestActions.로그인_상태로_출발역과_도착역_입력;
import static nextstep.subway.path.acceptance.PathAcceptanceTestActions.조회_불가능;
import static nextstep.subway.path.acceptance.PathAcceptanceTestActions.최단_경로가_조회됨;
import static nextstep.subway.path.acceptance.PathAcceptanceTestActions.출발역과_도착역_입력;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_등록되어_있음;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.time.LocalDateTime;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


@DisplayName("지하철 경로 조회")
public class PathAcceptanceTest extends AcceptanceTest {

    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;
    private StationResponse 공사중인역;
    private StationResponse 공사중인역2;

    /**
     * <p>교대역 ---*2호선* (10)--- 강남역</p>
     * |                        |
     * <p>*3호선* (5)          *신분당선* (10)</p>
     * |                        |
     * <p>남부터미널역 -- *3호선* (3)-- 양재</p>
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);
        공사중인역 = 지하철역_등록되어_있음("공사중인역").as(StationResponse.class);
        공사중인역2 = new StationResponse(10L, "공사중인역2", LocalDateTime.now(), LocalDateTime.now());

        신분당선 = 지하철_노선_등록되어_있음(new LineRequest("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10, 900))
                .as(LineResponse.class);
        이호선 = 지하철_노선_등록되어_있음(new LineRequest("이호선", "bg-red-600", 교대역.getId(), 강남역.getId(), 10, 1000))
                .as(LineResponse.class);
        삼호선 = 지하철_노선_등록되어_있음(new LineRequest("삼호선", "bg-red-600", 교대역.getId(), 남부터미널역.getId(), 5))
                .as(LineResponse.class);
        지하철_노선에_지하철역_등록되어_있음(삼호선, 남부터미널역, 양재역, 3);
    }

    /**
     * <p> Given 지하철 노선이 등록되어 있다
     * <p>  And 회원 등록되어 있음
     * <p>  And 로그인 되어있음
     * <p>
     * <p> When 출발역과 도착역을 입력하면
     * <p>
     * <p> Then 최단 경로가 조회된다
     * <p>  And 총 거리도 함께 응답함
     * <p>  And 지하철 이용 요금도 함께 응답함
     */
    @DisplayName("로그인 상태로 최단 경로를 조회한다")
    @Test
    void findShortestPathWithAuth() {
        //given 회원 등록되어 있음
        ExtractableResponse<Response> createMemberResponse = 회원_생성을_요청(EMAIL, PASSWORD, AGE);
        회원_생성됨(createMemberResponse);

        //given 로그인 되어있음
        ExtractableResponse<Response> loginResponse = 로그인_요청(EMAIL, PASSWORD);
        String accessToken = loginResponse.jsonPath().getString("accessToken");

        //when
        ExtractableResponse<Response> response = 로그인_상태로_출발역과_도착역_입력(accessToken, 남부터미널역, 강남역);

        //then
        최단_경로가_조회됨(response, 13, 1350, 남부터미널역, 양재역, 강남역);
    }

    /**
     * <p> Given 지하철 노선이 등록되어 있다
     * <p>
     * <p> When 출발역과 도착역을 입력하면
     * <p>
     * <p> Then 최단 경로가 조회된다
     * <p>  And 총 거리도 함께 응답함
     * <p>  And 지하철 이용 요금도 함께 응답함
     */
    @DisplayName("비로그인 상태로 최단 경로를 조회한다")
    @Test
    void findShortestPathWithOutAuth() {
        //when
        ExtractableResponse<Response> response = 출발역과_도착역_입력(남부터미널역, 강남역);

        //then
        최단_경로가_조회됨(response, 13, 2350, 남부터미널역, 양재역, 강남역);
    }

    /**
     * Given 지하철 노선이 등록되어 있다
     * <p>
     * When 출발역과 도착역을 동일하게 입력하면
     * <p>
     * Then 조회할 수 없다
     */
    @DisplayName("시작역과 도착역이 같으면 조회할 수 없다")
    @Test
    void sameSourceAndTarget() {
        //when
        ExtractableResponse<Response> response = 출발역과_도착역_입력(남부터미널역, 남부터미널역);

        //then
        조회_불가능(response);
    }

    /**
     * Given 지하철 노선이 등록되어 있다
     * <p>
     * When 출발역과 도착역의 연결정보가 없으면
     * <p>
     * Then 조회할 수 없다
     */
    @DisplayName("출발역과 도착역의 연결정보가 없으면 조회할 수 없다")
    @Test
    void notAddedEdge() {
        //when
        ExtractableResponse<Response> response = 출발역과_도착역_입력(공사중인역, 남부터미널역);

        //then
        조회_불가능(response);
    }

    /**
     * Given 지하철 노선이 등록되어 있다
     * <p>
     * When 존재하지 않은 출발역이나 도착역을 조회하면
     * <p>
     * Then 조회할 수 없다
     */
    @DisplayName("존재하지 않은 출발역이나 도착역은 조회할 수 없다")
    @Test
    void notExistStation() {
        //when
        ExtractableResponse<Response> response = 출발역과_도착역_입력(남부터미널역, 공사중인역2);

        //then
        조회_불가능(response);
    }
}
