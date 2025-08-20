package com.KDT.mosi.web.controller.review;

import com.KDT.mosi.domain.entity.board.Bbs;
import com.KDT.mosi.domain.review.svc.ReviewSVC;
import com.KDT.mosi.web.api.ApiResponse;
import com.KDT.mosi.web.api.ApiResponseCode;
import com.KDT.mosi.web.form.board.bbs.SaveApi;
import com.KDT.mosi.web.form.review.TagInfo;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequestMapping("/api/review")
@RestController
@RequiredArgsConstructor
public class ApiReviewController {

  private final ReviewSVC reviewSVC;

  //게시글 추가
  @PostMapping
  public ResponseEntity<ApiResponse<Bbs>> add(
      @RequestBody @Valid SaveApi saveApi,
      HttpSession session
  ) {
    Long memberId = (Long) session.getAttribute("loginMemberId");
    saveApi.setMemberId(memberId);
    Bbs bbs = new Bbs();
    BeanUtils.copyProperties(saveApi, bbs);
    Long id = bbsSVC.save(bbs);
    if (saveApi.getUploadGroup() != null) {
      bbsUploadSVC.bindGroupToBbs(id,saveApi.getUploadGroup());
    }
    Optional<Bbs> optionalBbs = bbsSVC.findById(id);
    Bbs findedBbs = optionalBbs.orElseThrow();

    ApiResponse<Bbs> postBbsApiResponse = ApiResponse.of(ApiResponseCode.SUCCESS, findedBbs);

    return ResponseEntity.status(HttpStatus.CREATED).body(postBbsApiResponse);
  }



  // 태그 반환: 공용 + 해당 카테고리 (카테고리가 없으면 빈 배열 [])
  @GetMapping("/tag/{category}")
  public ResponseEntity<ApiResponse<List<TagInfo>>> getTags(
      @PathVariable("category") String category
  ) {
    List<TagInfo> tags = reviewSVC.findTagList(category);
    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, tags));
  }
}
