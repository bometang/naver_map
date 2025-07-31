package com.KDT.mosi.web.controller.map.naverMap;

import com.KDT.mosi.domain.naverMap.svc.naverMapSVC;
import com.KDT.mosi.web.config.naverMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@RequestMapping("/map")
public class MapController {
  private final naverMapSVC naverMapSVC;
  private final naverMap props;

  /** AJAX용 geocode 엔드포인트 */
  @GetMapping("/geocode")
  @ResponseBody
  public ResponseEntity<String> geocode(@RequestParam("query") String query) throws Exception {
    String resultJson = naverMapSVC.geocode(query);
    return ResponseEntity.ok(resultJson);
  }

  /** 지도 페이지 뷰 */
  @GetMapping
  public String mapPage(Model model) {
    model.addAttribute("naverMapProps", props);
    return "naverMap/page";
  }
}
