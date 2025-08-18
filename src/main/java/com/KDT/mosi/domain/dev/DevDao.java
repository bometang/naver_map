package com.KDT.mosi.domain.dev;

public interface DevDao {
  /**
   * PRODUCT_ID를 고정값(예: 1)으로 넣고,
   * PRODUCT(FILE_*)는 지정한 파일에서 읽은 값으로 채우며,
   * PRODUCT_IMAGE는 이미지 N장을 한꺼번에 넣는다.
   */
  int insertProductWithImagesFixedId(
      long fixedProductId,
      long memberId,
      String category,
      String title,
      String guideYn,
      long normalPrice,
      long guidePrice,
      long salesPrice,
      long salesGuidePrice,
      int totalDay,
      int totalTime,
      long reqMoney,
      String sleepInfo,
      String transportInfo,
      String foodInfo,
      String reqPeople,
      String target,
      String stucks,
      String description,
      String detail,
      String baseDirForProductFile, // PRODUCT.FILE_* 로 들어갈 대표 파일 경로
      String productFileName,       // 예: "제7회 K-디지털 ... .pdf"
      String baseDirForImages,      // 이미지 파일들이 있는 폴더
      java.util.List<String> imageFileNames // 예: ["바다1.jpg","바다2.jpg","바다3.jpg","바다4.jpg"]
  );
}
