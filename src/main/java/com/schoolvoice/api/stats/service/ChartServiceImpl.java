package com.schoolvoice.api.stats.service;

import com.schoolvoice.api.stats.dto.CategoryStatisticsResponse;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ChartServiceImpl implements ChartService {

    private final StatisticsService statisticsService;

    public ChartServiceImpl(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @Override
    public byte[] createCategoryAverageChart() {
        List<CategoryStatisticsResponse> statistics = statisticsService.getCategoryStatistics();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (CategoryStatisticsResponse statistic : statistics) {
            double average = 0;
            if (statistic.averageRating() != null) {
                average = statistic.averageRating().doubleValue();
            }
            dataset.addValue(average, "Average Rating", statistic.category().name());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                "Category Average Rating",
                "Category",
                "Rating (1-5)",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        CategoryPlot plot = chart.getCategoryPlot();
        plot.getRangeAxis().setRange(0, 5);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            ChartUtils.writeChartAsPNG(out, chart, 600, 400);
            return out.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("차트 이미지 생성에 실패했습니다.", e);
        }
    }
}
