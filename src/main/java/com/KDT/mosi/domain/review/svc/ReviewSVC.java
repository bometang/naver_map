package com.KDT.mosi.domain.review.svc;

import com.KDT.mosi.domain.entity.review.ReviewInfo;
import com.KDT.mosi.domain.entity.review.ReviewProduct;
import com.KDT.mosi.web.form.review.ReviewTag;

import java.util.List;
import java.util.Optional;

public interface ReviewSVC {
  //상품 요약 정보 확인
  Optional<ReviewProduct> summaryFindById(Long orderItemId, Long loginId);

  //상품 구매자 확인
  Optional<ReviewInfo> findBuyerIdByOrderItemId(Long id);

  //태그 반환
  List<ReviewTag> findTagList(String category);

  //태그 카테고리 확인
  boolean categoryFind(String category);
}
