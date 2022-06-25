package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DijkstraPathFinder implements PathFinder {

    private final List<Line> lines;

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph
            = new WeightedMultigraph<>(DefaultWeightedEdge.class);

    public DijkstraPathFinder(List<Line> lineList) {
        lines = new ArrayList<>(lineList);
    }

    @Override
    public PathResponse findShortestPath(Station sourceStation, Station targetStation) {
        ensureNotSameStation(sourceStation, targetStation);
        ensureStationInRoute(sourceStation, targetStation);

        initGraphVertex();
        initGraphEdgeWeight();

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation);
        if (shortestPath == null) {
            throw new IllegalArgumentException("최단거리가 존재하지 않습니다.");
        }

        return new PathResponse(shortestPath.getVertexList(), (int) shortestPath.getWeight());
    }

    private void ensureStationInRoute(Station sourceStation, Station targetStation) {
        if (isNotInRoute(sourceStation)) {
            throw new IllegalArgumentException("출발역이 존재하지 않습니다.");
        }

        if (isNotInRoute(targetStation)) {
            throw new IllegalArgumentException("도착역이 존재하지 않습니다.");
        }
    }

    private boolean isNotInRoute(Station sourceStation) {
        return lines.stream()
                .map(Line::getStations)
                .flatMap(Collection::stream)
                .noneMatch(station -> station.equals(sourceStation));
    }

    private void ensureNotSameStation(Station sourceStation, Station targetStation) {
        if (sourceStation.equals(targetStation)) {
            throw new IllegalArgumentException("출발역과 도착역이 동일합니다.");
        }
    }

    private void initGraphEdgeWeight() {
        lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .forEach(section -> graph.setEdgeWeight(
                        graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

    private void initGraphVertex() {
        lines.stream()
                .map(Line::getStations)
                .flatMap(Collection::stream)
                .forEach(graph::addVertex);
    }
}
