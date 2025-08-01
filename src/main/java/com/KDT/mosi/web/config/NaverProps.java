package com.KDT.mosi.web.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "naver")
@Getter
@Setter
public class NaverProps {

  private Gateway gateway;
  private Open open;

  @Getter @Setter
  public static class Gateway {
    private String baseUrl;
    private String clientId;
    private String accessKey;
    private String secretKey;
  }

  @Getter @Setter
  public static class Open {
    private String baseUrl;
    private String clientId;
    private String clientSecret;
  }
}