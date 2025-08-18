package com.KDT.mosi.web.form.review;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.sql.Date;

@Getter
@AllArgsConstructor
public class ReviewProduct {
  private Long productId;
  private String nickname;
  private String category;
  private String title;
  private Date updateDate;
  private String imageDataUri;
}
