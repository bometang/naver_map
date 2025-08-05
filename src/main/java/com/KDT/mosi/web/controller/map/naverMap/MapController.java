package com.KDT.mosi.web.controller.map.naverMap;

import com.KDT.mosi.domain.entity.map.naverMap.AddressInfo;
import com.KDT.mosi.domain.map.kakaoMap.kakaoMapSVC;
import com.KDT.mosi.domain.map.naverMap.svc.naverMapSVC;
import com.KDT.mosi.web.api.ApiResponse;
import com.KDT.mosi.web.api.ApiResponseCode;
import com.KDT.mosi.web.config.NaverProps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/map")
public class MapController {
  private final naverMapSVC naverMapSVC;
  private final NaverProps props;
  private final kakaoMapSVC kakaoMapSVC;

  @GetMapping("/dev")
  public ResponseEntity<ApiResponse<List<AddressInfo>>> searchByKeyword(
      @RequestParam("query") String query,
      @RequestParam(value="start", required=false) String start
  ) {
    // 기본 중심 좌표
    double lat = 37.5665, lng = 126.9780;

    // start 파싱
    if (start != null && start.contains(",")) {
      String[] p = start.split(",");
      lat = Double.parseDouble(p[0]);
      lng = Double.parseDouble(p[1]);
    }

    List<AddressInfo> list = kakaoMapSVC.fetchAddresses(query, lat, lng);
    return ResponseEntity.ok(ApiResponse.of(ApiResponseCode.SUCCESS, list));
  }



  /** AJAX용 geocode 엔드포인트 */
  @GetMapping("/geocode")
  @ResponseBody
  public ResponseEntity<String> geocode(@RequestParam("query") String query) throws Exception {
    String resultJson = naverMapSVC.geocode(query);
    log.info("controller에서 받은 resultJson={}", resultJson);
    return ResponseEntity.ok(resultJson);
  }

  /** 지도 페이지 뷰 */
  @GetMapping
  public String mapPage(Model model) {
    model.addAttribute("naverMapProps", props);
    return "naverMap/page";
  }
}
