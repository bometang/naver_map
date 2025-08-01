package com.KDT.mosi.domain.entity.map.naverMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)      // items 외 나머지 필드 무시
public class LocalSearchRes {

  private List<Item> items;                      // 응답의 items 배열

  @Getter @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)    // title, mapx, mapy 등 무시
  public static class Item {                     // ← 반드시 public static
    private String address;       // 지번 주소
    private String roadAddress;   // 도로명 주소
  }
}
