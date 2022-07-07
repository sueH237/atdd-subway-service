package nextstep.subway.path.domain;

import java.util.List;
import java.util.Optional;
import nextstep.subway.line.domain.Distance;
import nextstep.subway.line.domain.Line;

public class FareCalculator {
    private static final int BASE_FARE = 1250;

    protected FareCalculator() {
    }

    public int calculate(Distance distance, List<Line> lines) {
        int totalFare = BASE_FARE;
        int distanceExcessFare = DistanceFarePolicy.calculateTotalExcessFare(distance);
        int lineExcessFare = LineFarePolicy.calculateExcessFare(lines);

        return totalFare + distanceExcessFare + lineExcessFare;
    }

    public int calculate(Distance distance, List<Line> lines, Integer age) {
        int totalFare = calculate(distance, lines);

        Optional<AgeFarePolicy> applicableAgeFarePolicy = AgeFarePolicy.findApplicableAgeFarePolicy(age);
        if (applicableAgeFarePolicy.isPresent()) {
            totalFare = applicableAgeFarePolicy.get().applyPolicy(totalFare);
        }

        return totalFare;
    }
}