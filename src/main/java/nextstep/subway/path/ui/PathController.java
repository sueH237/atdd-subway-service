package nextstep.subway.path.ui;

import nextstep.subway.auth.domain.AuthenticationPrincipal;
import nextstep.subway.auth.domain.LoginMember;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.application.PathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/paths")
public class PathController {
    private final PathService pathService;

    public PathController(PathService pathService) {
        this.pathService = pathService;
    }

    @GetMapping
    public ResponseEntity<PathResponse> searchShortestPath(@AuthenticationPrincipal(required = false) LoginMember loginMember,
                                                           @RequestParam("source") Long source,
                                                           @RequestParam("target") Long target) {
        return ResponseEntity
                .ok(pathService.searchShortestPath(loginMember, source, target));
    }
}
