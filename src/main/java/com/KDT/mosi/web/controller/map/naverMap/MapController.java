package com.KDT.mosi.web.controller.map.naverMap;

import com.KDT.mosi.domain.entity.map.naverMap.AddressInfo;
import com.KDT.mosi.domain.naverMap.svc.naverMapSVC;
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

  @GetMapping("/dev")
  public ResponseEntity<ApiResponse<List<AddressInfo>>> searchByKeyword(
      @RequestParam("query") String query
  ) {
    log.info("controller received query=[{}]", query);
    List<AddressInfo> addressList = naverMapSVC.fetchAddresses(query);
    log.info("controller returning AddressInfo count=[{}]", addressList.size());
    return ResponseEntity
        .ok(ApiResponse.of(ApiResponseCode.SUCCESS, addressList));
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
