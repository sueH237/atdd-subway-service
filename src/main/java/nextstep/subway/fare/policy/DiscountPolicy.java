package nextstep.subway.fare.policy;

import nextstep.subway.fare.domain.Fare;

public interface DiscountPolicy {
    Fare discount(int fare);
}
