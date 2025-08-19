package com.KDT.mosi.domain.review.svc;

import com.KDT.mosi.domain.common.svc.CodeSVC;
import com.KDT.mosi.domain.dto.CodeDTO;
import com.KDT.mosi.domain.entity.review.ReviewInfo;
import com.KDT.mosi.domain.entity.review.ReviewProduct;
import com.KDT.mosi.domain.review.dao.ReviewDAO;
import com.KDT.mosi.web.form.review.ReviewTag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

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
  public List<ReviewTag> findTagList(String category) {
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
}
