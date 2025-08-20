package com.KDT.mosi.domain.review.svc;

import com.KDT.mosi.domain.common.svc.CodeSVC;
import com.KDT.mosi.domain.dto.CodeDTO;
import com.KDT.mosi.domain.entity.review.Review;
import com.KDT.mosi.domain.entity.review.ReviewInfo;
import com.KDT.mosi.domain.entity.review.ReviewProduct;
import com.KDT.mosi.domain.entity.review.ReviewTag;
import com.KDT.mosi.domain.review.dao.ReviewDAO;
import com.KDT.mosi.web.form.review.TagInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewSVCImpl implements ReviewSVC{
  private final ReviewDAO reviewDAO;
  private final CodeSVC codeSVC;

  private static final Map<String, String> KEY_TO_CODE = Map.of(
      "area",             "B0101",
      "pet",              "B0102",
      "restaurant",       "B0103",
      "culture_history",  "B0104",
      "season_nature",    "B0105",
      "silver_disables",  "B0106"
  );

  @Override
  public Optional<ReviewProduct> summaryFindById(Long orderItemId, Long loginId) {
    ReviewInfo reviewInfo = findBuyerIdByOrderItemId(orderItemId)
        .orElseThrow(() -> new AccessDeniedException("주문 아이템이 없거나 접근 불가"));

    if (!Objects.equals(reviewInfo.getBuyerId(), loginId)) {
      throw new AccessDeniedException("본인 주문이 아닙니다.");
    }
    if (!"N".equals(reviewInfo.getReviewed())) {
      throw new AccessDeniedException("이미 작성한 리뷰 입니다.");
    }

    return reviewDAO.summaryFindById(reviewInfo.getProductId())
        .map(rp -> { rp.setOptionType(reviewInfo.getOptionType()); return rp; });
  }

  @Override
  public Optional<ReviewInfo> findBuyerIdByOrderItemId(Long id) {

    return reviewDAO.findBuyerIdByOrderItemId(id);
  }

  @Override
  public List<TagInfo> findTagList(String category) {
    if (!categoryFind(category)) {
      return List.of(); // 카테고리가 없으면 빈 리스트
    }
    String in = category.trim();
    String codeId = KEY_TO_CODE.getOrDefault(in.toLowerCase(), in.toUpperCase());

    return reviewDAO.findTagList(codeId);
  }

  @Override
  public boolean categoryFind(String category) {
    if (category == null || category.isBlank()) return false;


    String in = category.trim();
    String codeId = KEY_TO_CODE.getOrDefault(in.toLowerCase(), in.toUpperCase());

    List<CodeDTO> list = codeSVC.getB01();

    return list != null && list.stream()
        .anyMatch(c -> codeId.equalsIgnoreCase(c.getCodeId()));
  }

  @Override
  public Long reviewSave(List<Long> ids, Review review, String category) {
    // 1) 카테고리별 허용 태그 조회
    List<TagInfo> allowedTags = reviewDAO.findTagList(category);

    Set<Long> allowedIds = allowedTags.stream()
        .map(TagInfo::getTagId)
        .collect(Collectors.toSet());

    // 2) 요청된 태그 검증
    for (Long id : ids) {
      if (!allowedIds.contains(id)) {
        throw new IllegalArgumentException("허용되지 않는 태그입니다. tagId=" + id);
      }
    }

    // 3) 리뷰 저장
    Long reviewId = reviewDAO.saveReview(review);

    // 4) 리뷰-태그 매핑 저장
    Long sortOrder = 1L;
    for (Long tagId : ids) {
      ReviewTag rt = new ReviewTag();
      rt.setReviewId(reviewId);
      rt.setTagId(tagId);
      rt.setSortOrder(sortOrder++);
      reviewDAO.saveReviewTag(rt);
    }

    return reviewId;
  }
}
