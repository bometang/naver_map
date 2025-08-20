package com.KDT.mosi.domain.entity.review;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {
  private Long reviewId;
  private Long productId;
  private Long buyerId;
  private Long orderItemId;
  private String content;
  private double score;
  private String sellerRecoYN;
  private String status;
  private Date createDate;
  private Date updateDate;

}
