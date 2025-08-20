package com.KDT.mosi.domain.review.dao;

import com.KDT.mosi.domain.entity.review.Review;
import com.KDT.mosi.domain.entity.review.ReviewInfo;
import com.KDT.mosi.domain.entity.review.ReviewProduct;
import com.KDT.mosi.domain.entity.review.ReviewTag;
import com.KDT.mosi.web.form.review.TagInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Slf4j
@RequiredArgsConstructor
@Repository
public class ReviewDAOImpl implements ReviewDAO{

  final private NamedParameterJdbcTemplate template;

  @Override
  public Optional<ReviewProduct> summaryFindById(Long id) {

    StringBuffer sql = new StringBuffer();
    sql.append("SELECT p.product_id AS product_id,category,title,p.create_DATE AS create_date,nickname,mime_Type,image_data  ");
    sql.append("FROM product p ");
    sql.append("LEFT JOIN product_image i ");
    sql.append("  ON p.PRODUCT_ID = i.PRODUCT_ID ");
    sql.append(" AND i.image_order = ( ");
    sql.append("       SELECT MIN(pi.image_order) FROM product_image pi WHERE pi.product_id = p.product_id ");
    sql.append("     ) ");
    sql.append("LEFT JOIN MEMBER m ON p.MEMBER_ID = m.MEMBER_ID ");
    sql.append("WHERE p.product_ID = :productId ");;

    SqlParameterSource param = new MapSqlParameterSource().addValue("productId",id);

    ReviewProduct reviewProduct = null;
    try {
      reviewProduct = template.queryForObject(sql.toString(), param, BeanPropertyRowMapper.newInstance(ReviewProduct.class));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }

    return Optional.of(reviewProduct);
  }

  @Override
  public Optional<ReviewInfo> findBuyerIdByOrderItemId(Long id) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT buyer_id,product_id,option_type,REVIEWED ");
    sql.append("FROM ORDER_ITEMS i ");
    sql.append("JOIN orders o ");
    sql.append("ON i.order_id = o.order_id ");
    sql.append("WHERE i.order_item_id= :orderItemId");

    ReviewInfo reviewInfo;
    try {
      SqlParameterSource param = new MapSqlParameterSource().addValue("orderItemId",id);
      reviewInfo = template.queryForObject(sql.toString(), param,  BeanPropertyRowMapper.newInstance(ReviewInfo.class));
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }

    return Optional.of(reviewInfo);
  }

  @Override
  public List<TagInfo> findTagList(String category) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT tag_id, label, slug ");
    sql.append("FROM tag ");
    sql.append("WHERE useyn = 'Y' ");
    sql.append("AND (commonyn = 'Y' OR tcategory = :category) ");
    sql.append("ORDER BY ");
    sql.append("DECODE(commonyn, 'Y', 0, 1), ");
    sql.append("DECODE(recoyn,  'Y', 0, 1), ");
    sql.append("tag_id ");

    SqlParameterSource param = new MapSqlParameterSource().addValue("category", category);
    //db요청
    List<TagInfo> list = template.query(sql.toString(), param, BeanPropertyRowMapper.newInstance(TagInfo.class));

    return list;
  }

  @Override
  public Long saveReview(Review review) {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO review(REVIEW_ID,PRODUCT_ID,BUYER_ID,ORDER_ID,content,SCORE) ");
    sql.append("VALUES (REVIEW_SEQ.nextval,:productId,:buyerId,:orderId,:content,:score) ");

    SqlParameterSource param = new BeanPropertySqlParameterSource(review);
    KeyHolder keyHolder = new GeneratedKeyHolder();
    template.update(sql.toString(), param, keyHolder, new String[]{"REVIEW_ID"});

    Number key = (Number)keyHolder.getKeys().get("REVIEW_ID");
    return key.longValue();
  }

  @Override
  public int saveReviewTag(ReviewTag reviewTag) {
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO REVIEW_TAG(REVIEW_ID,tag_id,sort_order) ");
    sql.append("VALUES (:reviewId,:tagId,:sortOrder) ");

    SqlParameterSource param = new BeanPropertySqlParameterSource(reviewTag);
    int result = template.update(sql.toString(), param);
    return result;
  }

  @Override
  public boolean findTagId(Long id,String category) {
    StringBuffer sql = new StringBuffer();
    sql.append("SELECT count(*) ");
    sql.append("FROM TAG ");
    sql.append("WHERE tag_id = :id ");
    sql.append("AND (tcategory = :category OR commonyn='Y') ");

    SqlParameterSource param = new MapSqlParameterSource()
        .addValue("category", category)
        .addValue("id", id);
    int i = template.queryForObject(sql.toString(), param, Integer.class);
    if(i>0) return true;

    return false;
  }
}
