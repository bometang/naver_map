package com.KDT.mosi.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "naver.map")
public class naverMap {
  private String clientId;

  private Rest rest = new Rest();
  @Data
  public static class Rest {
    private String accessKey;
    private String secretKey;
    private String baseUrl;
  }
}
