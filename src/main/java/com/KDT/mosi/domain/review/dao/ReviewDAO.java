package com.KDT.mosi.domain.review.dao;

import com.KDT.mosi.domain.entity.review.ReviewInfo;
import com.KDT.mosi.domain.entity.review.ReviewProduct;
import com.KDT.mosi.web.form.review.ReviewTag;

import java.util.List;
import java.util.Optional;

public interface ReviewDAO {
  //상품 요약 정보 확인
  Optional<ReviewProduct> summaryFindById(Long id);

  //상품 구매자 확인
  Optional<ReviewInfo> findBuyerIdByOrderItemId(Long id);

  //태그 반환
  List<ReviewTag> findTagList(String category);

  //구매 상품 id 조회

  //리뷰 저장

  //리뷰 삭제

  //리뷰 목록

  //리뷰 작성 유무

  //리뷰 카테고리,공통 태그 조회


}
