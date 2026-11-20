package com.schoolvoice.api.stats.controller;

import com.schoolvoice.api.review.entity.ReviewCategory;
import com.schoolvoice.api.stats.dto.CategoryStatisticsResponse;
import com.schoolvoice.api.stats.dto.OverallStatisticsResponse;
import com.schoolvoice.api.stats.dto.RatingCount;
import com.schoolvoice.api.stats.service.StatisticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/categories")
    public List<CategoryStatisticsResponse> getCategories() {
        return statisticsService.getCategoryStatistics();
    }

    @GetMapping("/overall")
    public OverallStatisticsResponse getOverall() {
        return statisticsService.getOverallStatistics();
    }

    @GetMapping("/distribution")
    public List<RatingCount> getDistribution(@RequestParam("category") ReviewCategory category) {
        return statisticsService.getDistribution(category);
    }
}
