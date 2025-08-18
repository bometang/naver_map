package com.KDT.mosi.domain.review.dao;

import com.KDT.mosi.domain.entity.review.ReviewProduct;

import java.util.Optional;

public interface ReviewDAO {
  //상품 요약 정보 확인
  Optional<ReviewProduct> summaryFindById(Long id);

  //구매 상품 id 조회

  //리뷰 저장

  //리뷰 삭제

  //리뷰 목록

  //리뷰 작성 유무

  //리뷰 카테고리,공통 태그 조회


}
