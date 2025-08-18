package com.KDT.mosi.domain.review.dao;

import com.KDT.mosi.domain.entity.review.ReviewProduct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Slf4j
@RequiredArgsConstructor
@Repository
public class ReviewDAOImpl implements ReviewDAO{

  final private NamedParameterJdbcTemplate template;

  @Override
  public Optional<ReviewProduct> summaryFindById(Long id) {

    StringBuffer sql = new StringBuffer();
    sql.append("SELECT p.product_id AS product_id,category,title,p.create_DATE AS create_date,nickname,image_data  ");
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
}
