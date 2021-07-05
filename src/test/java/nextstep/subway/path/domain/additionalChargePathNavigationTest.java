package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class additionalChargePathNavigationTest {

    private List<Line> lines;

    @BeforeEach
    void setUp() {
        lines = new ArrayList<>();
    }

    @Test
    void _특정호선은_추가요금이_붙는다() {
        Station 양평 = new Station(10L, "양평"), 가평 = new Station(11L, "가평");
        Station 광나루역 = new Station(12L, "광나루역"), 천호역 = new Station(13L, "천호역");
        Station 오리역 = new Station(14L, "오리역"), 미금역 = new Station(15L, "미금역");

        Line 경춘선 = new Line("경춘선", "gs-12345", 900, 양평, 가평, 15); //1.5Km
        Line 사호선 = new Line("사호선", "gs-12245", 500, 광나루역, 천호역, 20); //2Km
        Line 분당선 = new Line("분당선", "gs-12545", 0, 오리역, 미금역, 20); //2Km
        lines.addAll(Lists.list(경춘선, 사호선, 분당선));

        Path path = PathNavigation.by(lines).findShortestPath(양평, 가평);
        assertThat(path.fee()).isEqualTo(1250 + 900); // 1.5Km + additionalCharge 900

        Path path2 = PathNavigation.by(lines).findShortestPath(광나루역, 천호역);
        assertThat(path2.fee()).isEqualTo(1250 + 500); // 2km + additionalCharge 500

        Path path3 = PathNavigation.by(lines).findShortestPath(오리역, 미금역);
        assertThat(path3.fee()).isEqualTo(1250); // 2km + no additionalCharge
    }

    @Test
    void 추가요금이_있는_노선은_거리비례제가_적용된다() {
        Station 도농 = new Station(1L, "도농"), 구리 = new Station(2L, "구리");
        Line 중앙선 = new Line("중앙선", "gs-111", 900, 도농, 구리, 100); //10Km
        lines.add(중앙선);
        Path path0 = PathNavigation.by(lines).findShortestPath(도농, 구리);
        assertThat(path0.distance()).isEqualTo(100);
        assertThat(path0.fee()).isEqualTo(1250 + 900); // 10Km + additionalCharge 900

        Station 양평 = new Station(10L, "양평"), 가평 = new Station(11L, "가평");
        Line 경춘선 = new Line("경춘선", "gs-12345", 900, 양평, 가평, 120); //12Km
        lines.add(경춘선);
        Path path = PathNavigation.by(lines).findShortestPath(양평, 가평);
        assertThat(path.distance()).isEqualTo(120);
        assertThat(path.fee()).isEqualTo(1250 + 900 + 100); // 12Km + additionalCharge 900

        Station 광나루역 = new Station(12L, "광나루역"), 천호역 = new Station(13L, "천호역");
        Line 사호선 = new Line("사호선", "gs-12245", 900, 광나루역, 천호역, 200); //20Km
        lines.add(사호선);
        Path path2 = PathNavigation.by(lines).findShortestPath(광나루역, 천호역);
        assertThat(path2.distance()).isEqualTo(200);
        assertThat(path2.fee()).isEqualTo(1250 + 900 + 200); // 20Km + additionalCharge 900
        lines.add(사호선);

        Station 오리역 = new Station(14L, "오리역"), 미금역 = new Station(15L, "미금역");
        Station 죽전역 = new Station(16L, "죽전역");

        Line 분당선 = new Line("분당선", "gs-12545", 900, 미금역, 오리역, 500); //50Km
        분당선.addSection(new Section(분당선, 오리역, 죽전역, 580)); // 58Km
        lines.add(분당선);
        Path path3 = PathNavigation.by(lines).findShortestPath(오리역, 미금역);
        assertThat(path3.distance()).isEqualTo(500);
        assertThat(path3.fee()).isEqualTo(1250 + 900 + 800); // 50Km + additionalCharge 900

        Path path4 = PathNavigation.by(lines).findShortestPath(오리역, 죽전역);
        assertThat(path4.distance()).isEqualTo(580);
        assertThat(path4.fee()).isEqualTo(1250 + 900 + 900); // 58Km + additionalCharge 900
    }

    @Test
    void _추가요금이_붙는_호선을_거치는_가장_높은_추가요금이_붙는다() {
        Station 양평 = new Station(10L, "양평"), 가평 = new Station(11L, "가평");
        Line 경춘선 = new Line("경춘선", "gs-12345", 900, 양평, 가평, 10);

        Station 상봉 = new Station(4L, "상봉"), 구리 = new Station(3L, "구리");
        lines.add(new Line("중앙선", "gs-1123", 0, 구리, 상봉, 10));

        경춘선.addSection(new Section(경춘선, 상봉, 양평, 10));
        lines.add(경춘선);

        Path path = PathNavigation.by(lines).findShortestPath(구리, 가평);
        assertThat(path.fee()).isEqualTo(1250 + 900);
    }
}