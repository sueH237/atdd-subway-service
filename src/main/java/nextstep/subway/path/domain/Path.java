package nextstep.subway.path.domain;

import nextstep.subway.station.domain.Station;

import java.util.List;

public class Path {
    private final List<Station> stations;
    private final int distance;

    public static Path of(List<Station> stations, int distance) {
        return new Path(stations, distance);
    }

    public Path(List<Station> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    public int getFare(int additionalFare, int age) {
        int totalFare = DistanceFarePolicy.BASIC_FARE + DistanceFarePolicy.calculateOverFare(distance) + additionalFare;
        return totalFare - FareSalePolicy.calculateSaleByAge(age, totalFare);
    }
}
