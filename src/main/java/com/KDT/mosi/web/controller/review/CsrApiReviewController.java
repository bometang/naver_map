package com.KDT.mosi.web.controller.review;

import com.KDT.mosi.domain.entity.Product;
import com.KDT.mosi.domain.product.dao.ProductDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Optional;

@Controller
@RequestMapping("/review")
@RequiredArgsConstructor
public class CsrApiReviewController {
  ProductDAO productDAO;
  @GetMapping("/{id}")
  public String bbs(
      @PathVariable("id") Long id,
      Authentication authentication) {
    Optional<Product> productItem = productDAO.findById(id);
    return "review/review_writeForm";
  }

}
