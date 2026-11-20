package com.schoolvoice.api.export.service;

import com.schoolvoice.api.review.entity.Review;
import com.schoolvoice.api.review.entity.ReviewCategory;
import com.schoolvoice.api.review.repository.ReviewRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ExcelExportServiceTest {

    @InjectMocks
    private ExcelExportServiceImpl excelExportService;

    @Mock
    private ReviewRepository reviewRepository;

    @Test
    @DisplayName("정상 추출: 바이트 배열을 엑셀로 복원하여 시트, 헤더, 리뷰 및 통계값을 확인한다.")
    void exportReviewsToExcel_NormalData() throws Exception {
        // given
        List<Review> dummyReviews = Arrays.asList(
                Review.create(ReviewCategory.TEACHING, 5, "좋아요", "hash1"),
                Review.create(ReviewCategory.TEACHING, 3, "보통", "hash2"),
                Review.create(ReviewCategory.FACILITY, 4, "깨끗함", "hash3")
        );
        given(reviewRepository.findAll()).willReturn(dummyReviews);

        // when
        byte[] excelBytes = excelExportService.exportReviewsToExcel();

        // then: 바이트를 다시 Workbook으로 열어 검증
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {

            // 1. 시트 존재 및 이름 검증
            assertThat(workbook.getNumberOfSheets()).isEqualTo(2);
            Sheet reviewSheet = workbook.getSheet("리뷰 목록");
            Sheet statSheet = workbook.getSheet("통계");
            assertThat(reviewSheet).isNotNull();
            assertThat(statSheet).isNotNull();

            // 2. 리뷰 시트 헤더 및 데이터 검증 (토큰이 없는지 확인)
            Row headerRow = reviewSheet.getRow(0);
            assertThat(headerRow.getCell(0).getStringCellValue()).isEqualTo("항목(Category)");
            assertThat(headerRow.getCell(1).getStringCellValue()).isEqualTo("평점(Rating)");

            Row firstDataRow = reviewSheet.getRow(1);
            assertThat(firstDataRow.getCell(0).getStringCellValue()).isEqualTo("TEACHING");
            assertThat(firstDataRow.getCell(1).getNumericCellValue()).isEqualTo(5.0);
            assertThat(firstDataRow.getCell(2).getStringCellValue()).isEqualTo("좋아요");

            // 3. 통계 시트 데이터 검증 (TEACHING 평가 수 2, 평균 4.0)
            boolean foundTeachingStat = false;
            for (int i = 1; i <= statSheet.getLastRowNum(); i++) {
                Row row = statSheet.getRow(i);
                if (row != null && "TEACHING".equals(row.getCell(0).getStringCellValue())) {
                    assertThat(row.getCell(1).getNumericCellValue()).isEqualTo(2.0); // 평가 수
                    assertThat(row.getCell(2).getNumericCellValue()).isEqualTo(4.0); // 평균
                    foundTeachingStat = true;
                    break;
                }
            }
            assertThat(foundTeachingStat).isTrue();
        }
    }

    @Test
    @DisplayName("빈 데이터 정책: 데이터가 없어도 헤더가 포함된 엑셀 파일이 정상적으로 생성된다.")
    void exportReviewsToExcel_EmptyData() throws Exception {
        // given
        given(reviewRepository.findAll()).willReturn(Collections.emptyList());

        // when
        byte[] excelBytes = excelExportService.exportReviewsToExcel();

        // then
        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet reviewSheet = workbook.getSheet("리뷰 목록");

            // 데이터 로우는 없고 헤더(row 0)만 존재해야 함
            assertThat(reviewSheet.getLastRowNum()).isEqualTo(0);
            assertThat(reviewSheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("항목(Category)");
        }
    }
}