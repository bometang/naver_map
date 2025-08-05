package com.KDT.mosi.domain.entity.map.kakaoMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor                    // ★ 추가
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoLocalSearchRes {
  private List<Document> documents;

  @Getter @Setter
  @NoArgsConstructor                  // ★ 추가
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Document {
    @JsonProperty("address_name")
    private String addressName;
    @JsonProperty("road_address_name")
    private String roadAddressName;
    private String x;
    private String y;
  }
}
