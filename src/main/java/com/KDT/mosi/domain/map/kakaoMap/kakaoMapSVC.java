package com.KDT.mosi.domain.map.kakaoMap;

import com.KDT.mosi.domain.entity.map.naverMap.AddressInfo;

import java.util.List;

public interface kakaoMapSVC {
  List<AddressInfo> fetchAddresses(String keyword, double lat, double lng);

}
