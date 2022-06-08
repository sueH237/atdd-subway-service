package nextstep.subway.favorite;

import static nextstep.subway.auth.acceptance.AuthAcceptanceTestMethod.로그인_요청;
import static nextstep.subway.favorite.FavoriteAcceptanceTestMethod.즐겨찾기_삭제_실패;
import static nextstep.subway.favorite.FavoriteAcceptanceTestMethod.즐겨찾기_삭제_요청;
import static nextstep.subway.favorite.FavoriteAcceptanceTestMethod.즐겨찾기_삭제됨;
import static nextstep.subway.favorite.FavoriteAcceptanceTestMethod.즐겨찾기_생성_실패;
import static nextstep.subway.favorite.FavoriteAcceptanceTestMethod.즐겨찾기_생성_요청;
import static nextstep.subway.favorite.FavoriteAcceptanceTestMethod.즐겨찾기_생성됨;
import static nextstep.subway.favorite.FavoriteAcceptanceTestMethod.즐겨찾기_조회_요청;
import static nextstep.subway.favorite.FavoriteAcceptanceTestMethod.즐겨찾기_조회됨;
import static nextstep.subway.line.acceptance.LineAcceptanceTestMethod.지하철_노선_등록되어_있음;
import static nextstep.subway.line.acceptance.LineSectionAcceptanceTestMethod.지하철_노선에_지하철역_등록되어_있음;
import static nextstep.subway.member.MemberAcceptanceTestMethod.회원_등록됨;
import static nextstep.subway.station.StationAcceptanceTest.지하철역_등록되어_있음;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Arrays;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.dto.TokenRequest;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.favorite.dto.FavoriteRequest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.member.dto.MemberRequest;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("즐겨찾기 관련 기능")
class FavoriteAcceptanceTest extends AcceptanceTest {

    public static final String EMAIL = "email@email.com";
    public static final String PASSWORD = "password";
    public static final int AGE = 20;

    public static final String NEW_EMAIL = "new_email@email.com";
    public static final String NEW_PASSWORD = "new_password";
    public static final int NEW_AGE = 200;

    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;

    private TokenResponse 토큰;

    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = 지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = 지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);

        LineRequest 신분당선_Request = LineRequest.of("신분당선", "bg-red-600", 강남역.getId(), 양재역.getId(), 10);
        LineRequest 이호선_Request = LineRequest.of("이호선", "bg-red-600", 교대역.getId(), 강남역.getId(), 10);
        LineRequest 삼호선_Request = LineRequest.of("삼호선", "bg-red-600", 남부터미널역.getId(), 양재역.getId(), 5);

        신분당선 = 지하철_노선_등록되어_있음(신분당선_Request).as(LineResponse.class);
        이호선 = 지하철_노선_등록되어_있음(이호선_Request).as(LineResponse.class);
        삼호선 = 지하철_노선_등록되어_있음(삼호선_Request).as(LineResponse.class);

        지하철_노선에_지하철역_등록되어_있음(삼호선.getId(), SectionRequest.of(교대역.getId(), 남부터미널역.getId(), 3));

        회원_등록됨(MemberRequest.of(EMAIL, PASSWORD, AGE));
        토큰 = 로그인_요청(TokenRequest.of(EMAIL, PASSWORD)).as(TokenResponse.class);
    }

    /**
     * Gvien. 즐겨찾기 요청정보 생성
     * When. 즐겨찾기 생성
     * Then. 즐겨찾기 생성 완료
     */
    @DisplayName("즐겨찾기를 생성한다.")
    @Test
    void createFavorite() {
        // given
        FavoriteRequest favoriteRequest = FavoriteRequest.of(교대역.getId(), 양재역.getId());

        // when
        ExtractableResponse<Response> response = 즐겨찾기_생성_요청(토큰, favoriteRequest);

        // then
        즐겨찾기_생성됨(response);
    }

    /**
     * Gvien. 즐겨찾기 정보 등록
     * When. 즐겨찾기 조회
     * Then. 등록된 즐겨찾기 조회 완료
     */
    @DisplayName("즐겨찾기 목록을 조회한다.")
    @Test
    void showFavorite() {
        // given
        ExtractableResponse<Response> createResponse1 = 즐겨찾기_생성_요청(토큰, FavoriteRequest.of(교대역.getId(), 양재역.getId()));
        ExtractableResponse<Response> createResponse2 = 즐겨찾기_생성_요청(토큰, FavoriteRequest.of(교대역.getId(), 남부터미널역.getId()));

        // when
        ExtractableResponse<Response> response = 즐겨찾기_조회_요청(토큰);

        // then
        즐겨찾기_조회됨(response, Arrays.asList(createResponse1, createResponse2));
    }

    /**
     * Gvien. 즐겨찾기 정보 등록
     * When. 즐겨찾기 삭제
     * Then. 등록한 즐겨찾기 정보가 삭제된다.
     */
    @DisplayName("즐겨찾기를 삭제한다.")
    @Test
    void removeFavorite() {
        // given
        ExtractableResponse<Response> createdResponse = 즐겨찾기_생성_요청(토큰, FavoriteRequest.of(교대역.getId(), 양재역.getId()));

        // when
        ExtractableResponse<Response> response = 즐겨찾기_삭제_요청(토큰, createdResponse);

        // then
        즐겨찾기_삭제됨(response);
    }

    /**
     * Given. 등록되지 않은 새로운 역을 만든다.
     * When. 즐겨찾기를 생성한다.
     * Then. 즐겨찾기 생성에 실패한다.
     */
    @DisplayName("존재하지 않은 역을 즐겨찾기로 등록할 수 없다.")
    @Test
    void exceptionCreateFavorite01() {
        // given
        Station 수서역 = Station.from("수서역");
        Station 판교역 = Station.from("판교역");

        // when
        ExtractableResponse<Response> createdResponse = 즐겨찾기_생성_요청(토큰, FavoriteRequest.of(수서역.getId(), 판교역.getId()));

        // then
        즐겨찾기_생성_실패(createdResponse);
    }

    @DisplayName("내가 만든 즐겨찾기가 아닌 즐겨찾기를 제거할 수 없다.")
    @Test
    void exceptionRemoveFavorite01() {
        // when
        ExtractableResponse<Response> createdResponse = 즐겨찾기_생성_요청(토큰, FavoriteRequest.of(강남역.getId(), 양재역.getId()));

        // then
        즐겨찾기_생성됨(createdResponse);

        // given
        회원_등록됨(MemberRequest.of(NEW_EMAIL, NEW_PASSWORD, NEW_AGE));
        TokenResponse new토큰 = 로그인_요청(TokenRequest.of(NEW_EMAIL, NEW_PASSWORD)).as(TokenResponse.class);

        // when
        ExtractableResponse<Response> deleteResponse = 즐겨찾기_삭제_요청(new토큰, createdResponse);

        // then
        즐겨찾기_삭제_실패(deleteResponse);
    }
}