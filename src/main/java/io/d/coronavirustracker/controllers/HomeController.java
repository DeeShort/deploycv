package io.d.coronavirustracker.controllers;

import io.d.coronavirustracker.CoronavirusTrackerApplication;
import io.d.coronavirustracker.models.LocationStates;
import io.d.coronavirustracker.services.CorVirDatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CorVirDatService corVirDatService;

    @GetMapping("/")
    public String home(Model model){
        List<LocationStates> allStats = corVirDatService.getAllStats();
        int totalReportedCases=allStats.stream().mapToInt(stat->stat.getLatestTotalCases()).sum();
        int totalChanges=allStats.stream().mapToInt(stat->stat.getDiffFromPrevDay()).sum();
        model.addAttribute("locationStates", allStats);
        model.addAttribute("totalReportedCases", totalReportedCases);
        model.addAttribute("totalChanges", totalChanges);

        return"home";
    }
}
