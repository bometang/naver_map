package com.KDT.mosi.domain.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
@RequiredArgsConstructor
public class DevDaoImpl implements DevDao{

  private final DataSource dataSource;

  private static final String SQL_DELETE_IMAGES_BY_PRODUCT =
      "DELETE FROM PRODUCT_IMAGE WHERE PRODUCT_ID = ?";

  private static final String SQL_DELETE_PRODUCT_BY_ID =
      "DELETE FROM PRODUCT WHERE PRODUCT_ID = ?";

  private static final String SQL_INSERT_PRODUCT =
      "INSERT INTO PRODUCT ( " +
          " PRODUCT_ID, MEMBER_ID, CATEGORY, TITLE, GUIDE_YN, " +
          " NORMAL_PRICE, GUIDE_PRICE, SALES_PRICE, SALES_GUIDE_PRICE, " +
          " TOTAL_DAY, TOTAL_TIME, REQ_MONEY, SLEEP_INFO, TRANSPORT_INFO, FOOD_INFO, " +
          " REQ_PEOPLE, TARGET, STUCKS, DESCRIPTION, DETAIL, " +
          " FILE_NAME, FILE_TYPE, FILE_SIZE, FILE_DATA, " +
          " PRICE_DETAIL, GPRICE_DETAIL, STATUS, CREATE_DATE, UPDATE_DATE " +
          ") VALUES ( " +
          " ?, ?, ?, ?, ?, " +                 //  1~ 5
          " ?, ?, ?, ?, " +                    //  6~ 9
          " ?, ?, ?, ?, ?, " +                 // 10~14
          " ?, ?, ?, ?, ?, " +                 // 15~19
          " ?, ?, ?, ?, " +                    // 20~23
          " ?, ?, ?, ?, SYSTIMESTAMP, NULL " +    // 24~27, (CREATE/UPDATE는 상수라 바인딩 X)
          ")";

  private static final String SQL_SELECT_MAX_IMAGE_ID =
      "SELECT NVL(MAX(IMAGE_ID),0) FROM PRODUCT_IMAGE";

  private static final String SQL_INSERT_IMAGE = """
      INSERT INTO PRODUCT_IMAGE
        (IMAGE_ID, PRODUCT_ID, IMAGE_DATA, IMAGE_ORDER, FILE_NAME, FILE_SIZE, MIME_TYPE)
      VALUES (?,?,?,?,?,?,?)
      """;

  @Override
  public int insertProductWithImagesFixedId(
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
      String baseDirForProductFile,
      String productFileName,
      String baseDirForImages,
      java.util.List<String> imageFileNames
  ) {
    try (Connection con = dataSource.getConnection()) {
      con.setAutoCommit(false);

      // 0) 같은 PRODUCT_ID가 있으면 깨끗이 지움(테스트/초기 적재용)
      try (PreparedStatement ps = con.prepareStatement(SQL_DELETE_IMAGES_BY_PRODUCT)) {
        ps.setLong(1, fixedProductId);
        ps.executeUpdate();
      }
      try (PreparedStatement ps = con.prepareStatement(SQL_DELETE_PRODUCT_BY_ID)) {
        ps.setLong(1, fixedProductId);
        ps.executeUpdate();
      }

      // 1) PRODUCT INSERT (FILE_*은 실제 파일에서 읽어 채움)
      Path pFile = Path.of(baseDirForProductFile, productFileName);
      byte[] pBytes = Files.readAllBytes(pFile);
      String pMime  = Files.probeContentType(pFile);
      long   pSize  = Files.size(pFile);

      try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_PRODUCT)) {
        int i = 1;
        ps.setLong(i++, fixedProductId);      // PRODUCT_ID = 1(고정)
        ps.setLong(i++, memberId);
        ps.setString(i++, category);
        ps.setString(i++, title);
        ps.setString(i++, guideYn);
        ps.setLong(i++, normalPrice);
        ps.setLong(i++, guidePrice);
        ps.setLong(i++, salesPrice);
        ps.setLong(i++, salesGuidePrice);
        ps.setInt(i++, totalDay);
        ps.setInt(i++, totalTime);
        ps.setLong(i++, reqMoney);
        ps.setString(i++, sleepInfo);
        ps.setString(i++, transportInfo);
        ps.setString(i++, foodInfo);
        ps.setString(i++, reqPeople);
        ps.setString(i++, target);
        ps.setString(i++, stucks);
        ps.setString(i++, description);
        ps.setString(i++, detail);
        ps.setString(i++, productFileName);
        ps.setString(i++, pMime);
        ps.setLong(i++, pSize);
        ps.setBytes(i++, pBytes);
        ps.setString(i++, "첨부파일");   // PRICE_DETAIL (요청 주신 원문 유지)
        ps.setString(i++, "ㄹㄹㄹㄹ");  // GPRICE_DETAIL
        ps.setString(i++, "판매중");     // STATUS
        ps.executeUpdate();
      }

      // 2) IMAGE_ID 시작값 계산 (중복 방지)
      long startImageId = 0;
      try (PreparedStatement ps = con.prepareStatement(SQL_SELECT_MAX_IMAGE_ID);
           ResultSet rs = ps.executeQuery()) {
        if (rs.next()) startImageId = rs.getLong(1);
      }
      long nextImageId = startImageId + 1;

      // 3) PRODUCT_IMAGE 배치 INSERT
      try (PreparedStatement psImg = con.prepareStatement(SQL_INSERT_IMAGE)) {
        int order = 1;
        for (String fname : imageFileNames) {
          Path ip = Path.of(baseDirForImages, fname);
          byte[] bytes = Files.readAllBytes(ip);
          String mime  = Files.probeContentType(ip);
          long   size  = Files.size(ip);

          int j = 1;
          psImg.setLong(j++, nextImageId++);                       // IMAGE_ID 자동 증가(중복 방지)
          psImg.setLong(j++, fixedProductId);                      // PRODUCT_ID = 1
          psImg.setBytes(j++, bytes);                              // IMAGE_DATA (BLOB)
          psImg.setInt(j++, order++);                              // IMAGE_ORDER = 1..N
          psImg.setString(j++, fname);                             // FILE_NAME
          psImg.setLong(j++, size);                                // FILE_SIZE
          psImg.setString(j++, mime != null ? mime : "application/octet-stream"); // MIME_TYPE
          psImg.addBatch();
        }
        psImg.executeBatch();
      }

      con.commit();
      // 성공 건수(대략) 반환: 상품 1건 + 이미지 N건
      return 1 + imageFileNames.size();

    } catch (Exception e) {
      throw new RuntimeException("insertProductWithImagesFixedId 실패", e);
    }
  }
}
