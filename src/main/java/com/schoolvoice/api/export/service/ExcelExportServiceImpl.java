package com.schoolvoice.api.export.service;

import com.schoolvoice.api.review.entity.Review;
import com.schoolvoice.api.review.entity.ReviewCategory;
import com.schoolvoice.api.review.repository.ReviewRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ExcelExportServiceImpl implements ExcelExportService {

    private final ReviewRepository reviewRepository;

    public ExcelExportServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public byte[] exportReviewsToExcel() {
        List<Review> reviews = reviewRepository.findAll();

        // 엑셀 워크북 리소스 자동 종료
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // 첫 번째 시트: 리뷰 목록 (토큰 제외)
            Sheet reviewSheet = workbook.createSheet("리뷰 목록");
            createReviewSheet(reviewSheet, reviews);

            // 두 번째 시트: 통계
            Sheet statSheet = workbook.createSheet("통계");
            createStatSheet(workbook, statSheet, reviews);

            workbook.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("엑셀 파일 생성 중 오류가 발생했습니다.", e);
        }
    }

    private void createReviewSheet(Sheet sheet, List<Review> reviews) {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("항목(Category)");
        headerRow.createCell(1).setCellValue("평점(Rating)");
        headerRow.createCell(2).setCellValue("이유(Reason)");
        headerRow.createCell(3).setCellValue("작성일(CreatedAt)");

        int rowIdx = 1;
        for (Review review : reviews) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(review.getCategory().name());
            row.createCell(1).setCellValue(review.getRating());
            row.createCell(2).setCellValue(review.getReason());
            row.createCell(3).setCellValue(review.getCreatedAt().toString());
        }

        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createStatSheet(Workbook workbook, Sheet sheet, List<Review> reviews) throws IOException {
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("항목(Category)");
        headerRow.createCell(1).setCellValue("평가 수(Count)");
        headerRow.createCell(2).setCellValue("평균 평점(Average)");

        Map<ReviewCategory, List<Review>> grouped = reviews.stream()
                .collect(Collectors.groupingBy(Review::getCategory));

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        int rowIdx = 1;

        for (Map.Entry<ReviewCategory, List<Review>> entry : grouped.entrySet()) {
            String categoryName = entry.getKey().name();
            long count = entry.getValue().size();
            double average = entry.getValue().stream()
                    .mapToInt(Review::getRating)
                    .average()
                    .orElse(0.0);

            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(categoryName);
            row.createCell(1).setCellValue(count);
            row.createCell(2).setCellValue(Math.round(average * 10.0) / 10.0);

            dataset.addValue(average, "Average Rating", categoryName);
        }

        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }

        insertChartImage(workbook, sheet, dataset, rowIdx + 2);
    }

    private void insertChartImage(Workbook workbook, Sheet sheet, DefaultCategoryDataset dataset, int startRow) throws IOException {
        JFreeChart barChart = ChartFactory.createBarChart(
                "Category Average Rating", "Category", "Rating (1-5)",
                dataset, PlotOrientation.VERTICAL, false, true, false
        );

        BufferedImage chartImage = barChart.createBufferedImage(600, 400);
        ByteArrayOutputStream chartOut = new ByteArrayOutputStream();
        ImageIO.write(chartImage, "png", chartOut);
        byte[] imageBytes = chartOut.toByteArray();

        int pictureIdx = workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG);
        CreationHelper helper = workbook.getCreationHelper();
        Drawing<?> drawing = sheet.createDrawingPatriarch();

        ClientAnchor anchor = helper.createClientAnchor();
        anchor.setCol1(0);
        anchor.setRow1(startRow);

        Picture picture = drawing.createPicture(anchor, pictureIdx);
        picture.resize();
    }
}