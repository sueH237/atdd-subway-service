package nextstep.subway.path.ui;

import nextstep.subway.path.application.PathService;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PathController {

    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping("/paths")
    public ResponseEntity<PathResponse> getShortestPath(
            @RequestParam(name = "source") String startStationId,
            @RequestParam(name = "target") String endStationId
    ){
        PathResponse pathResponse = pathService.findShortestPath(startStationId,endStationId);
       return ResponseEntity.ok(pathResponse);
    }
}
