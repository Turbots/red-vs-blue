package io.pivotal.workshop.redvsblue;

import io.pivotal.workshop.redvsblue.game.Team;
import io.pivotal.workshop.redvsblue.game.scoring.ScoringService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    private final ScoringService scoringService;

    public HomeController(ScoringService scoringService) {
        this.scoringService = scoringService;
    }

    @GetMapping
    public String home(Model model) {
        model.addAttribute("redTeam", this.scoringService.getRankingForTeam(Team.RED));
        model.addAttribute("blueTeam", this.scoringService.getRankingForTeam(Team.BLUE));

        model.addAttribute("score", this.scoringService.getScore());

        return "index";
    }
}
