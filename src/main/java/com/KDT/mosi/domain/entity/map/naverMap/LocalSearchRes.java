package com.KDT.mosi.domain.entity.map.naverMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalSearchRes {
  private List<Item> items;
  @Getter @Setter
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Item {
    private String address;
    private String roadAddress;
    private String mapx;
    private String mapy;
  }
}
