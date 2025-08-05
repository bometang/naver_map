package com.KDT.mosi.domain.map.naverMap.svc;

import com.KDT.mosi.domain.entity.map.naverMap.AddressInfo;

import java.util.List;

public interface naverMapSVC {
  /** GET 방식으로 geocode 호출 */
  String geocode(String query);

  List<AddressInfo> fetchAddresses(String keyword, double lat, double lng);
}
