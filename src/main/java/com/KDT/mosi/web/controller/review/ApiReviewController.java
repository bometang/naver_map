package com.KDT.mosi.web.controller.review;

import com.KDT.mosi.domain.review.svc.ReviewSVC;
import com.KDT.mosi.web.api.ApiResponse;
import com.KDT.mosi.web.api.ApiResponseCode;
import com.KDT.mosi.web.form.review.ReviewTag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/api/review")
@RestController
@RequiredArgsConstructor
public class ApiReviewController {

  private final ReviewSVC reviewSVC;

  // 태그 반환: 공용 + 해당 카테고리 (카테고리가 없으면 빈 배열 [])
  @GetMapping("/tag/{category}")
  public ResponseEntity<ApiResponse<List<ReviewTag>>> getTags(
      @PathVariable("category") String category
  ) {
    List<ReviewTag> tags = reviewSVC.findTagList(category);
    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, tags));
  }
}
